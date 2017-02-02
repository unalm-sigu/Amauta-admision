package pe.edu.lamolina.pivot.controller.test;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

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
    CargaAcademicaService cargaAcademicaService;

    @ResponseBody
    @RequestMapping("crearEvaluacionByExp")
    public String crearEvaluacionByExp() {

        GrupoSeccion grupoSeccion = new GrupoSeccion(161741);
        EvaluacionSeccion evaluacionSeccion = new EvaluacionSeccion(44);
        PlanCalificacion planCalificacion = new PlanCalificacion(86);

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

                    Evaluacion evaluacion = new Evaluacion();
                    evaluacion = evaluacionDAO.findByEvalExpSeccion(evaluacionExpandida.getId(), seccionEach.getId());
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

}
