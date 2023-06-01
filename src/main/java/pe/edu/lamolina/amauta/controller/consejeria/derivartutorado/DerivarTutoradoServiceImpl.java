package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoDerivadoAtencionDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoAtencionTutoradoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoRemitenteDerivacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.enums.consejeria.EstadoDerivacionEnum;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.TipoAtencionTutorado;
import pe.edu.lamolina.model.tutoria.TipoRemitenteDerivacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class DerivarTutoradoServiceImpl implements DerivarTutoradoService {

    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final AlumnoDerivadoAtencionDAO alumnoDerivadoAtencionDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final TipoAtencionTutoradoDAO tipoAtencionTutoradoDAO;
    private final TipoRemitenteDerivacionDAO tipoRemitenteDerivacionDAO;

    private final PlanTutoriaService planTutoriaService;
    private final VerificadorService verificadorService;

    @Override
    public List<AlumnoDerivadoAtencion> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean tienePermiso = planTutoriaService.tienePermiso(alumno, ciclo, ds);
        if (!tienePermiso) {
            return new ArrayList();
        }
        List<AlumnoDerivadoAtencion> derivaciones = alumnoDerivadoAtencionDAO.allByDynatable(filter, alumno, ciclo);
        return derivaciones;
    }

    @Override
    public List<TipoAtencionTutorado> allTiposAtenciones() {
        return tipoAtencionTutoradoDAO.all();
    }

    @Override
    public List<Curso> allCursosMatriculados(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allByAlumnoCiclo(alumno, ciclo);

        return cursosMatriculados.stream()
                .map(matCurso -> matCurso.getCurso())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveDerivacion(Alumno alumno, AlumnoDerivadoAtencion derivacionForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de crear derivaciones a este estudiante");
        Assert.isNotNull(derivacionForm.getTipoAtencionTutorado(), "No ha indicado el tipo de atención");
        Assert.isNotNull(derivacionForm.getMotivoDerivacion(), "No ha indicado el motivo");

        TipoRemitenteDerivacion tipoRemitente = tipoRemitenteDerivacionDAO.findByCodigo("TUTOR");

        AlumnoDerivadoAtencion derivacion = new AlumnoDerivadoAtencion();
        derivacion.setEstadoEnum(EstadoDerivacionEnum.PENDIENTE);
        derivacion.setAlumno(alumno);
        derivacion.setCicloAcademico(ciclo);
        derivacion.setPersonaRemitente(ds.getPersona());
        derivacion.setTipoRemitenteDerivacion(tipoRemitente);
        derivacion.setTipoAtencionTutorado(derivacionForm.getTipoAtencionTutorado());
        derivacion.setCurso(derivacionForm.getCurso());
        derivacion.setMotivoDerivacion(derivacionForm.getMotivoDerivacion());
        derivacion.setUserRegistro(ds.getUsuario());
        derivacion.setFechaRegistro(today.toDate());
        alumnoDerivadoAtencionDAO.save(derivacion);

    }

}
