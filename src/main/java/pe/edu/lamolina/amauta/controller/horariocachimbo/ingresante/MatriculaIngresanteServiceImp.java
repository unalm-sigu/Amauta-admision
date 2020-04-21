package pe.edu.lamolina.amauta.controller.horariocachimbo.ingresante;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.amauta.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.amauta.controller.horariocachimbo.generar.HorarioCachimboGenerarService;
import pe.edu.lamolina.amauta.controller.responserest.ResponseRestService;
import pe.edu.lamolina.amauta.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.ConfigRecorridoIngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoActividadIngresanteDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioFallidoDAO;
import pe.edu.lamolina.amauta.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

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
    HelperMatriculaIngresanteService helperMatriculaIngresanteService;
    @Autowired
    HorarioCachimboGenerarService generarHorarioIngresanteService;
    @Autowired
    ResponseRestService responseRestService;
    @Autowired
    VisorMatricula visorMatricula;

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void matricularAlumno(
            AlumnoHorario aluHorario,
            Map<Long, List<Seccion>> mapSeccion,
            List<Curso> cursos,
            List<String> erroresAlu,
            HorarioCachimbos horario,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds) {

        Alumno alumno = aluHorario.getAlumno();

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivosByAlumnoCicloCursos(alumno, cicloAcademico, cursos);
        Map<Long, MatriculaCurso> mapCursoMatriculado = TypesUtil.convertListToMap("curso.id", cursosMatriculados);

        int existentes = 0;
        int matriculados = 0;
        int errores = 0;
        int loop = 0;

        boolean ok = true;
        for (Curso curso : cursos) {
            loop++;
            MatriculaCurso matCurso = mapCursoMatriculado.get(curso.getId());
            if (matCurso != null) {
                existentes++;
                continue;
            }

            List<Seccion> seccionesCurso = mapSeccion.get(curso.getId());
            Seccion seccion = getSeccionMatriculable(seccionesCurso);
            logger.info("Matricula cachimbo {} la seccion {} :::: es el {} de {}", alumno.getCodigo(), seccion.getCodigo2(), loop, cursos.size());

            TokenIngresante token = responseRestService.createToken(ds);
            JsonResponse json = responseRestService.matricularSeccionReservada(alumno, seccion, ds, token);
            logger.info("Respuesta cachimbo {}-{} :::: ok={}  msg={}", alumno.getCodigo(), seccion.getCodigo2(), json.getSuccess(), json.getMessage());

            if (json.getSuccess()) {
                matriculados++;

            } else if (!json.getSuccess()) {
                visorMatricula.getMensajes().add("Error con el alumno " + alumno.getCodigo() + ". " + json.getMessage());
                erroresAlu.add(json.getMessage());
                ok = false;
                errores++;
            }
        }

        logger.info("Finalizó cachimbo {} existentes={} matriculados={} errores={}", alumno.getCodigo(), existentes, matriculados, errores);

        visorMatricula.marcarAlumno(alumno);
        if (ok) {
            helperMatriculaIngresanteService.registrarMatricula(aluHorario, horario, ds);
        } else {
            helperMatriculaIngresanteService.registrarErroresAlumno(aluHorario, erroresAlu, ds);
        }
        helperMatriculaIngresanteService.registrarIncrementoHorario(horario, ds);

    }

    private Seccion getSeccionMatriculable(List<Seccion> secciones) {
        for (Seccion secc : secciones) {
            if (secc.isTipoSeccionTCUR()) {
            } else {
                return secc;
            }
        }
        return null;
    }

}
