package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
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
    CargaAcademicaService cargaAcademicaService;
    
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
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", planCalificacion.getId(), planEvaluaciones.size());
        for (Seccion seccionEach : secciones) {
            for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEnum().name());
                if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEnum())) {
                    
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
    @RequestMapping("calcularAllResumenEvaluacion/{grupoSeccion}")
    public String calcularAllResumenEvaluacion(@PathVariable("grupoSeccion") Long grupoSeccionId, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(null, null, ciclo);
        for (AlumnoEvaluacion alumnoEvaluacion : evaluacionesAlumno) {
            Alumno alumno = alumnoEvaluacion.getAlumno();
            Seccion seccion = alumnoEvaluacion.getEvaluacion().getSeccionResponsable();
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }
            
            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            if (grupoSeccionId.equals(grupoSeccion.getId())) {
                if (ObjectUtil.getParentTree(grupoSeccion, "planCalificacion.id") == null) {
                    continue;
                }
                cargaAcademicaService.recalcularAllResumenEvalAlumno(alumno, grupoSeccion);
            }
        }
        return "yeah";
    }
    
    @ResponseBody
    @RequestMapping("calcularAllResumenEvaluacion")
    public String calcularAllResumenEvaluacion(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(null, null, ciclo);
        for (AlumnoEvaluacion alumnoEvaluacion : evaluacionesAlumno) {
            Alumno alumno = alumnoEvaluacion.getAlumno();
            Seccion seccion = alumnoEvaluacion.getEvaluacion().getSeccionResponsable();
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }
            
            GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
            if (ObjectUtil.getParentTree(grupoSeccion, "planCalificacion.id") == null) {
                continue;
            }
            cargaAcademicaService.recalcularAllResumenEvalAlumno(alumno, grupoSeccion);
        }
        return "yeah";
    }
    
}
