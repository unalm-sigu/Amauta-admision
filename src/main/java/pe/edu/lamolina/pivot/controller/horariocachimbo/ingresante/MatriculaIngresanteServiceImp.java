package pe.edu.lamolina.pivot.controller.horariocachimbo.ingresante;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.controller.horariocachimbo.generar.HorarioCachimboGenerarService;
import pe.edu.lamolina.pivot.controller.responserest.ResponseRestService;
import pe.edu.lamolina.pivot.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfigRecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioFallidoDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MatriculaIngresanteServiceImp implements MatriculaIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;
    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;
    @Autowired
    ConfigRecorridoIngresanteDAO configRecorridoIngresanteDAO;
    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;
    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;
    @Autowired
    HorarioFallidoDAO horarioFallidoDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;
    @Autowired
    TipoActividadIngresanteDAO tipoActividadIngresanteDAO;
    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    HorarioCachimboGenerarService generarHorarioIngresanteService;
    @Autowired
    ResponseRestService responseRestService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarMatricula(AlumnoHorario aluHorario, HorarioCachimbos horario, DataSessionPivot ds) {

        aluHorario.setEstado(EstadoAlumnoHorarioEnum.MATR);
        aluHorario.setErrores(null);
        alumnoHorarioDAO.updateColumns(aluHorario, "estado", "errores");

        horario.setMatriculados(horario.getMatriculados() + 1);
        horarioCachimbosDAO.updateColumns(horario, "matriculados");

        RecorridoIngresante recorrido = recorridoIngresanteDAO.findByAlumnoCiclo(aluHorario.getAlumno(), horario.getCicloAcademico());
        if (recorrido == null) {
            return;
        }

        TipoActividadIngresante tipoActividadIngresante = tipoActividadIngresanteDAO.findCodigo(TipoActividadIngresanteEnum.MATRI);
        ActividadIngresante actividadMatri = actividadIngresanteDAO.findByRecorridoTipoActividad(recorrido, tipoActividadIngresante);
        if (actividadMatri != null) {
            if (actividadMatri.getEstadoEnum() != RecorridoIngresanteEstadoEnum.ACT) {
                actividadMatri.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
                actividadMatri.setFechaEjecucion(new Date());
                actividadMatri.setUserEjecucion(ds.getUsuario());
                actividadIngresanteDAO.update(actividadMatri);
            }
            return;
        }

        actividadMatri = new ActividadIngresante();
        actividadMatri.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
        actividadMatri.setFechaRegistro(new Date());
        actividadMatri.setRecorridoIngresante(recorrido);
        actividadMatri.setTipoActividadIngresante(tipoActividadIngresante);
        actividadMatri.setUserEjecucion(ds.getUsuario());
        actividadIngresanteDAO.save(actividadMatri);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIncrementoHorario(HorarioCachimbos horario, DataSessionPivot ds) {
        horarioCachimbosDAO.updateColumns(horario, "matriculados");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarErroresAlumno(AlumnoHorario aluHorario, List<String> erroresAlu, DataSessionPivot ds) {
        String errores = "";
        for (String msg : erroresAlu) {
            errores += errores.equals("") ? "" : "<br/>\n";
            errores += msg;
        }
        aluHorario.setErrores(errores);
        alumnoHorarioDAO.updateColumns(aluHorario, "errores");
    }

}
