package pe.edu.lamolina.amauta.controller.academico.registroborradoalumno;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.amauta.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.amauta.dao.academico.*;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.*;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.seguridad.Usuario;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class RegistroBorradoServiceImpl implements RegistroBorradoService {

    private final AlumnoDAO alumnoDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final RegistroBorradoAlumnoDAO registroBorradoAlumnoDAO;
    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AporteAlumnoCicloDAO aporteAlumnoCicloDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final PromedioService promedioService;
    private final AvanceCurricularService avanceCurricularService;

    @Override
    public List<Alumno> allActivoPregradoByNombre(String nombre) {
        return alumnoDAO.allByNamePregrado(nombre);
    }

    @Override
    public List<RegistroBorradoAlumno> allByDynatable(DynatableFilter filter) {
        return registroBorradoAlumnoDAO.allByDynatable(filter);
    }

    @Override
    public ObjectNode allHistorialByInfoAlumno(InfoAlumno infoAlumno) {
        ObjectNode infoAlumnoJson = new ObjectNode(JsonNodeFactory.instance);

        List<AlumnoCicloCurso> alumnoCiclosCurso = alumnoCicloCursoDAO.allByAlumnoAndCicloAcademico(infoAlumno.getIdAlumno(), infoAlumno.getIdCicloEstudiado());
        ArrayNode arrayAluCicloCurso = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCiclosCurso) {
            ObjectNode aluCicloCursoJson = JaneHelper.from(alumnoCicloCurso)
                    .only("estado,estadoEnum,nota")
                    .join("curso", "codigo,nombre")
                    .join("alumnoCiclo", "estadoEnum")
                    .join("alumnoCiclo.cicloAcademico", "descripcion")
                    .json();
            arrayAluCicloCurso.add(aluCicloCursoJson);
        }
        infoAlumnoJson.set("alumnoCicloCursos", arrayAluCicloCurso);
        return infoAlumnoJson;
    }

    @Override
    public ArrayNode allCiclosEstudiadosByAlumno(Alumno alumno) {
        List<AlumnoCicloCurso> alumnoCiclosCurso = alumnoCicloCursoDAO.allByAlumno(alumno);

        List<CicloAcademico> cicloAcademicos = alumnoCiclosCurso.
                stream().filter(x->x.getEstadoEnum().equals(EstadoMatriculaEnum.MAT)).map(x -> x.getAlumnoCiclo().getCicloAcademico()).distinct().
                collect(Collectors.toList());

        ArrayNode ciclosArray = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            ObjectNode cicloJson = JaneHelper.from(cicloAcademico)
                    .only("id,descripcion,descripcion2")
                    .json();
            ciclosArray.add(cicloJson);
        }

        return ciclosArray;
    }

    @Override
    @Transactional
    public void save(RegistroBorradoAlumno registro, DataSessionPivot ds) {
        List<AlumnoCicloCurso> alumnoCiclosCurso = alumnoCicloCursoDAO.allByAlumnoAndCicloAcademico(registro.getAlumno().getId(), registro.getCicloAfectado().getId());
        if (!alumnoCiclosCurso.isEmpty()) {
            alumnoCiclosCurso.forEach(alumnoCicloCurso -> {
                alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.NELI);
                alumnoCicloCurso.setRegistroActivo(0);
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            });
        } else {
            throw new PhobosException("No tiene historial el alumno");
        }

        AlumnoCiclo alumnoCiclo = alumnoCiclosCurso.get(0).getAlumnoCiclo();
        alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        alumnoCicloDAO.update(alumnoCiclo);

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoAndCiclo(registro.getAlumno(), registro.getCicloAfectado());
        if (matriculaResumen != null) {
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumen.setCreditosMatriculados(0);
            matriculaResumen.setCursosMatriculados(0);
            matriculaResumenDAO.update(matriculaResumen);
        } else {
            throw new PhobosException("Alumno no se encontrado matriculado en matricula resumen.");
        }

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByMatriculaResumen(matriculaResumen);
        List<MatriculaSeccion> matriculaSeccionMatriculados = matriculaSeccions.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .collect(Collectors.toList());

        if (!matriculaSeccionMatriculados.isEmpty()) {
            matriculaSeccionMatriculados.forEach(matriculaSeccion -> {
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                matriculaSeccion.setUserAnula(ds.getUsuario());
                matriculaSeccion.setFechaAnula(new Date());
                matriculaSeccionDAO.update(matriculaSeccion);
            });
        } else {
            throw new PhobosException("El alumno no tiene secciones matriculadas.");
        }

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
        List<MatriculaCurso> matriculaCursosMatriculados = matriculaCursos.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .collect(Collectors.toList());

        if (!matriculaCursosMatriculados.isEmpty()) {
            matriculaCursosMatriculados.forEach(matriculaCurso -> {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.NMAT);
                matriculaCurso.setUserAnula(ds.getUsuario());
                matriculaCurso.setFechaAnula(new Date());
                matriculaCursoDAO.update(matriculaCurso);
            });
        } else {
            throw new PhobosException("El alumno no tiene secciones matriculadas.");
        }

        RegistroBorradoAlumno registroBorradoAlumno = new RegistroBorradoAlumno();
        registroBorradoAlumno.setAlumno(registro.getAlumno());
        registroBorradoAlumno.setCicloAfectado(registro.getCicloAfectado());
        registroBorradoAlumno.setMotivo(registro.getMotivo());
        registroBorradoAlumno.setUserRegistra(ds.getUsuario());
        registroBorradoAlumno.setFechaRegistro(new Date());
        registroBorradoAlumnoDAO.save(registroBorradoAlumno);

        log.debug("Calculo de promedios");
        promedioService.calcularSituacionAcademicaNewSession(registro.getAlumno(), ds);

        log.debug("Calculo de avance curricular");
        avanceCurricularService.generarAvanceCurricularByAlumno(registro.getAlumno(), ds);


    }

    @Override
    public ObjectNode allHistorialEliminadoByInfoAlumno(InfoAlumno infoAlumno) {
        ObjectNode infoAlumnoJson = new ObjectNode(JsonNodeFactory.instance);

        List<AlumnoCicloCurso> alumnoCiclosCurso = alumnoCicloCursoDAO.allEliminadosByAlumnoAndCicloAcademico(infoAlumno.getIdAlumno(), infoAlumno.getIdCicloEstudiado());
        ArrayNode arrayAluCicloCurso = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCiclosCurso) {
            ObjectNode aluCicloCursoJson = JaneHelper.from(alumnoCicloCurso)
                    .only("estado,estadoEnum,nota")
                    .join("curso", "codigo,nombre")
                    .join("alumnoCiclo", "estadoEnum")
                    .join("alumnoCiclo.cicloAcademico", "descripcion")
                    .json();
            arrayAluCicloCurso.add(aluCicloCursoJson);
        }
        infoAlumnoJson.set("alumnoCicloCursos", arrayAluCicloCurso);
        return infoAlumnoJson;
    }
}
