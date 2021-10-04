package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import java.math.BigDecimal;
import java.util.Arrays;
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
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ViajeCursoDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.SubvencionViajeEstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.APROBADO;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.CREADO;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.DESAPROBADO;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.JUSTIFICADO;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.PENDIENTE;
import static pe.edu.lamolina.model.enums.ViajeCursoEstadoEnum.VB_JUSTIFICACION;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class SubvencionViajesServiceImp implements SubvencionViajesService {

    private final ColaboradorDAO colaboradorDAO;
    private final CursoDAO cursoDAO;
    private final DocenteSeccionDAO docenteSeccionDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final OficinaDAO oficinaDAO;
    private final SeccionDAO seccionDAO;
    private final ViajeCursoDAO viajeCursoDAO;

    @Override
    public List<ViajeCurso> allViajesByDynatble(Docente docente, CicloAcademico ciclo, DynatableFilter filter) {
        List<ViajeCurso> viajes = viajeCursoDAO.allByDocenteCiclo(docente, ciclo, filter);

        return viajes;
    }

    @Override
    @Transactional
    public void saveViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        this.validarDataViaje(viajeCurso, ciclo, docente);

        viajeCurso.setEstadoViajeEnum(CREADO);
        viajeCurso.setEstadoSubvencionEnum(SubvencionViajeEstadoEnum.SIN_PRESUPUESTO);
        viajeCurso.setCicloAcademico(ciclo);
        viajeCurso.setCantidadAlumnosAprobados(0);
        viajeCurso.setCantidadAlumnosRegistrados(0);
        viajeCurso.setCantidadAlumnosSeparados(0);
        viajeCurso.setCantidadAlumnosViajaron(0);

        viajeCurso.setImporteAlumno(BigDecimal.ZERO);
        viajeCurso.setImporteDevuelto(BigDecimal.ZERO);
        viajeCurso.setImporteEntregado(BigDecimal.ZERO);
        viajeCurso.setImporteProforma(BigDecimal.ZERO);
        viajeCurso.setImporteSolicitado(BigDecimal.ZERO);
        viajeCurso.setImporteUtilizado(BigDecimal.ZERO);

        viajeCurso.setDocenteCreador(docente);
        viajeCurso.setUserRegistro(ds.getUsuario());
        viajeCurso.setFechaCreacion(today.toDate());
        viajeCurso.setFechaRegistro(today.toDate());

        viajeCursoDAO.save(viajeCurso);

    }

    private void validarDataViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, Docente docente) {
        Seccion seccionForm = viajeCurso.getSeccion();
        Assert.isNotNull(seccionForm, "No ha indicado la sección");

        Curso cursoForm = viajeCurso.getCurso();
        Assert.isNotNull(seccionForm, "No ha indicado el curso");

        Alumno alumnoForm = viajeCurso.getAlumnoDelegado();
        Assert.isNotNull(alumnoForm, "No ha indicado el alumno delegado");

        Seccion seccionBD = seccionDAO.find(seccionForm);
        Assert.isNotNull(seccionBD, "No se pudo ubicar el registro de la sección");
        Assert.isTrue(seccionBD.getEstadoEnum() == SeccionEstadoEnum.ACT, "La sección no está activa");

        Curso cursoBD = seccionBD.getGrupoSeccion().getCurso();
        Assert.isTrue(cursoForm.getId().equals(cursoBD.getId()), "El registro del curso no coincide con el de la sección");

        CicloAcademico cicloBD = seccionBD.getGrupoSeccion().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloBD.getId()), "El ciclo de la sección no corresponde al ciclo activo de su sesión");

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccionBD);
        int regs = docentesSeccion.stream()
                .filter(x -> x.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .filter(x -> x.getDocente().getId().equals(docente.getId()))
                .collect(Collectors.toList())
                .size();
        Assert.isTrue(regs > 0, "Usted no está asignado como docente de esta sección");

        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allBySeccion(seccionBD);
        int mats = matriculadosSeccion.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .map(x -> x.getMatriculaResumen().getAlumno())
                .filter(x -> x.getId().equals(alumnoForm.getId()))
                .collect(Collectors.toList())
                .size();
        Assert.isTrue(mats > 0, "El alumno delegado no está matriculado en esta sección");
    }

    @Override
    @Transactional
    public void updateViaje(ViajeCurso viajeCursoForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        this.validarDataViaje(viajeCursoForm, ciclo, docente);

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == CREADO, "El registro del Viaje debe estar en estado " + CREADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        CicloAcademico cicloBD = viajeCursoBD.getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "Este registro corresponde a otro ciclo académico");

        viajeCursoBD.setCurso(viajeCursoForm.getCurso());
        viajeCursoBD.setSeccion(viajeCursoForm.getSeccion());
        viajeCursoBD.setAlumnoDelegado(viajeCursoForm.getAlumnoDelegado());
        viajeCursoBD.setCicloAcademico(ciclo);

        viajeCursoDAO.update(viajeCursoBD);
    }

    @Override
    @Transactional
    public void solicitarAprobarViaje(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == CREADO, "El registro del Viaje debe estar en estado " + CREADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        viajeCursoBD.setEstadoViajeEnum(PENDIENTE);
        viajeCursoBD.setFechaCreacion(today.toDate());
        viajeCursoDAO.update(viajeCursoBD);
    }

    @Override
    @Transactional
    public void aprobarViaje(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Persona persona = ds.getPersona();

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        Curso cursoBD = cursoDAO.find(viajeCursoBD.getCurso().getId());
        DepartamentoAcademico dpto = cursoBD.getDepartamentoAcademico();

        boolean validado = false;
        Oficina oficina = oficinaDAO.findByTipoOficinaDptoAcademico(TipoOficinaEnum.DPTO, dpto);
        Persona jefe = oficina.getPersonaJefe();
        if (jefe != null && jefe.getId().equals(persona.getId())) {
            validado = true;
        }

        if (!validado) {
            Persona encargado = oficina.getJefeEncargado();
            if (encargado != null && encargado.getId().equals(persona.getId())) {
                validado = true;
            }
        }

        Assert.isTrue(validado, "Usted no es Jefe del Departamento del curso seleccionado");

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficina, persona);

        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == PENDIENTE, "El registro del Viaje debe estar en estado " + PENDIENTE.getValue());

        List<ViajeCursoEstadoEnum> estados = Arrays.asList(APROBADO, DESAPROBADO);
        ViajeCursoEstadoEnum estadoForm = viajeCursoForm.getEstadoViajeEnum();
        Assert.isTrue(estados.contains(estadoForm), "Solo se aceptan Aprobar o Desaprobar");

        viajeCursoBD.setEstadoViajeEnum(estadoForm);
        viajeCursoBD.setFechaVoBoDepartamento(today.toDate());
        viajeCursoBD.setJefeDepartamentoVobo(colaborador);
        viajeCursoDAO.update(viajeCursoBD);

    }

    @Override
    @Transactional
    public void aprobarJustificacion(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == JUSTIFICADO, "El registro del Viaje debe estar en estado " + JUSTIFICADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        viajeCursoBD.setEstadoViajeEnum(VB_JUSTIFICACION);
        viajeCursoBD.setFechaVoBoAsistencia(today.toDate());
        viajeCursoDAO.update(viajeCursoBD);
    }

}
