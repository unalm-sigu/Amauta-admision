package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.pivot.controller.academico.calculonotas.CalculoNotasService;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("test")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Autowired
    CalculoNotasService calculoNotasService;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @ResponseBody
    @RequestMapping("crearEvaluacionByExp")
    public String crearEvaluacionByExp(@RequestParam("idGrupoSeccion") Long idGpoSecc) {

        //GrupoSeccion grupoSeccion = new GrupoSeccion(161741);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGpoSecc);
        if (grupoSeccion.getEvaluacionSecciones().isEmpty()) {
            return "No tiene evaluacionSeccion";
        }
        if (grupoSeccion.getEvaluacionSecciones().size() > 1) {
            return "No tiene varias evaluacionSecciones";
        }

        EvaluacionSeccion evaluacionSeccion = grupoSeccion.getEvaluacionSecciones().get(0);
        PlanCalificacion planCalificacion = grupoSeccion.getPlanCalificacion();

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("Cantidad de secciones para el grupo {}", secciones.size());
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", planCalificacion.getId(), planEvaluaciones.size());
        for (Seccion seccionEach : secciones) {
            for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());
                if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEvalEnum())) {

                    Evaluacion evaluacion = evaluacionDAO.findByEvalExpSeccion(evaluacionExpandida.getId(), seccionEach.getId());
                    if (evaluacion != null) {
                        continue;
                    }

                    evaluacion = new Evaluacion();
                    evaluacion.create(evaluacionSeccion, seccionEach, evaluacionExpandida);
                    if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {
                        evaluacion.setEvaluaciones(new ArrayList<>());
                        for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                            Evaluacion evaluacionChild = new Evaluacion();
                            evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                            evaluacionChild.setEvaluacionSuperior(evaluacion);
                            evaluacion.getEvaluaciones().add(evaluacionChild);
                        }
                    }
                    cargaAcademicaService.saveEvaluacion(evaluacion);

                }
            }
        }

        return "YEAH";

    }

    @ResponseBody
    @RequestMapping("/calcularAllResumenEvaluacion/{grupoSeccion}")
    public String calcularAllResumenEvaluacion(@PathVariable("grupoSeccion") Long grupoSeccionId, HttpSession session) {
        int loop = 1;
        visorCalculoNotas.iniciar();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allByGpoSeccion(new GrupoSeccion(grupoSeccionId), ciclo);
        for (MatriculaSeccion ms : alumnosSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            calculoNotasService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
            loop++;

        }

        return "yeah";
    }

    @ResponseBody
    @RequestMapping("calcularAllResumenEvaluacion")
    public String calcularAllResumenEvaluacion(HttpSession session) {
        int loop = 1;
        visorCalculoNotas.iniciar();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allByCiclo(ciclo);
        for (MatriculaSeccion ms : alumnosSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                continue;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            if (seccion.getGrupoSeccion().isEstadoGrupoCerrado()) {
                continue;
            }

            calculoNotasService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
            loop++;

        }
        return "yeah";
    }

}
