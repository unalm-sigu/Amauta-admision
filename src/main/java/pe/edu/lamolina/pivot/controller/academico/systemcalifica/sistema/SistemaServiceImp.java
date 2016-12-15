package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

@Service
@Transactional(readOnly = true)
public class SistemaServiceImp implements SistemaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoEvaluacionDAO tipoEvaluacionDAO;

    @Autowired
    SistemaNotasDAO sistemaNotasDAO;

    @Autowired
    PlanCalificacionDAO planCalificacionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    EvaluacionSeccionDAO evaluacionSeccionDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Override
    public List<TipoEvaluacion> allTipoEvaluacion() {
        return tipoEvaluacionDAO.all();
    }

    @Override
    public ObjectNode allTipoEvaluacionJson() {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        List<TipoEvaluacion> lstTipoEvaluacion = tipoEvaluacionDAO.all();
        for (TipoEvaluacion tipoEvaluacion : lstTipoEvaluacion) {
            ObjectNode jobj = new ObjectNode(JsonNodeFactory.instance);
            jobj.put("codigo", tipoEvaluacion.getCodigo());
            jobj.put("esDivisible", tipoEvaluacion.getEsDivisible());
            jobj.put("esNotaMinimaAnulable", tipoEvaluacion.isNotaMinimaAnulable());
            jobj.put("cantidadMaxima", tipoEvaluacion.getCantidadMaxima());
            json.put(tipoEvaluacion.getId().toString(), jobj.toString());
        }
        return json;
    }

    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }

    @Override
    @Transactional
    public void saveSistemaCalifica(PlanCalificacion planCalificacion) {

        planCalificacion.setEstadoEnum(EstadoPlanCalificaEnum.CRE);
        planCalificacion.setFechaRegistro(new Date());

        Integer totalWeight = BigDecimal.ZERO.intValue();

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            if (evaluacionPlan.getPesoEvaluacion() == null || evaluacionPlan.getPesoEvaluacion().intValue() == 0) {
                throw new PhobosException("Peso evaluacion incorrecto..");
            }
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }

            totalWeight += evaluacionPlan.getPesoTotal();
        }

        if (totalWeight != 100) {
            throw new PhobosException("Pesos total de las evaluaciones incorrecto.");
        }

        Long maxNumeroCorrelativo = planCalificacionDAO.maxNumeroCorrelativoPlanCalifica(planCalificacion.getDepartamentoAcademico().getId());
        maxNumeroCorrelativo = maxNumeroCorrelativo + 1;
        planCalificacion.setNumero(maxNumeroCorrelativo);

        planCalificacion.generateCodigo();

        planCalificacionDAO.save(planCalificacion);
    }

    @Override
    public List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter, DepartamentoAcademico dpto) {
        return planCalificacionDAO.allByDynatable(dynatableFilter, dpto);
    }

    @Override
    public PlanCalificacion findPlanCalificacion(Long idPlanCalificacion) {
        return planCalificacionDAO.find(idPlanCalificacion);
    }

    @Override
    @Transactional
    public void changeStatePlanCalificacion(Long idPLanCalificacion, String observacion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        PlanCalificacion planCalificacion = planCalificacionDAO.find(idPLanCalificacion);
        planCalificacion.setEstadoEnum(estadoPlanCalificaEnum);
        planCalificacion.setObservacion(observacion);
        if (EstadoPlanCalificaEnum.ACEP.equals(estadoPlanCalificaEnum)) {
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(idPLanCalificacion, null);
            evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
            evaluacionSeccionDAO.update(evaluacionSeccion);

            GrupoSeccion grupoSeccion = grupoSeccionDAO.find(evaluacionSeccion.getGrupoSeccion().getId());
            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(planCalificacion);
            grupoSeccionDAO.update(grupoSeccion);

            this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, EstadoPlanCalificaEnum.ACEP);

            List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
            logger.debug("Cantidad de secciones para el grupo {}", secciones.size());
            List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
            logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", idPLanCalificacion, planEvaluaciones.size());
            for (Seccion seccionEach : secciones) {
                for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                    logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                    logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                    logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEnum().name());
                    if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                            evaluacionExpandida.getTipoSeccionEnum())) {

                        Evaluacion evaluacion = new Evaluacion();
                        evaluacion.create(evaluacionSeccion, seccionEach, evaluacionExpandida);
                        if (evaluacionExpandida.getEvaluaciones() != null && !evaluacionExpandida.getEvaluaciones().isEmpty()) {
                            evaluacion.setEvaluaciones(new ArrayList<>());
                            for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluaciones()) {
                                Evaluacion evaluacionChild = new Evaluacion();
                                evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                                evaluacionChild.setEvaluacionSuperior(evaluacion);
                                evaluacion.getEvaluaciones().add(evaluacionChild);
                            }
                        }
                        evaluacionDAO.save(evaluacion);
                    }
                }
            }

        } else if (EstadoPlanCalificaEnum.RHZ.equals(estadoPlanCalificaEnum)
                || EstadoPlanCalificaEnum.OBS.equals(estadoPlanCalificaEnum)) {
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(idPLanCalificacion, null);
            evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
            evaluacionSeccionDAO.update(evaluacionSeccion);

            GrupoSeccion grupoSeccion = grupoSeccionDAO.find(evaluacionSeccion.getGrupoSeccion().getId());
            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(planCalificacion);
            grupoSeccionDAO.update(grupoSeccion);
        }
        planCalificacionDAO.update(planCalificacion);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        List<EvaluacionExpandida> evaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Evaluacion seccion {}, cantidad de pensiones expandidadas {}", evaluacionSeccion.getId(), evaluaciones.size());
        if (evaluaciones.isEmpty()) {
            logger.debug("no tiene evaluaciones, se creara las evaluaciones en base al plan calificacion {}", evaluacionSeccion.getPlanCalificacion().getId());

            List<EvaluacionPlan> evaluacionesPlanes = evaluacionPlanDAO.allByFilter(evaluacionSeccion.getPlanCalificacion().getId());
            logger.debug("Plan Calificacion {}, Cantidad de evaluaciones para el plan {} ", evaluacionSeccion.getPlanCalificacion().getId(), evaluacionesPlanes.size());
            for (EvaluacionPlan evaluacionPlan : evaluacionesPlanes) {

                BigDecimal peso = BigDecimal.ZERO;
                for (int i = 1; i <= evaluacionPlan.getCantidadEvaluaciones().intValue(); i++) {
                    EvaluacionExpandida evaluacion = new EvaluacionExpandida();
                    evaluacion.setAlumnoEvaluacion(null);
                    evaluacion.create(evaluacionSeccion, evaluacionPlan, i);

                    if (i == evaluacionPlan.getCantidadEvaluaciones().intValue()) {
                        BigDecimal pesoFinal = new BigDecimal(evaluacionPlan.getPesoTotal()).subtract(peso);
                        evaluacion.setPeso(pesoFinal);
                    }
                    peso = peso.add(evaluacionPlan.getPesoEvaluacion());
                    evaluacionExpandidaDAO.save(evaluacion);
                }
            }
        }

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
        grupoSeccion.setPlanCalificacion(evaluacionSeccion.getPlanCalificacion());
        grupoSeccionDAO.update(grupoSeccion);
    }

    @Override
    @Transactional
    public void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        changeStatePlanCalificacion(idPLanCalificacion, null, estadoPlanCalificaEnum);
    }

    @Override
    public List<Curso> allCursosByPlanCalifica(DynatableFilter dynatableFilter, Long planCalificacion, Long idDepartamentoAcademico) {
        return cursoDAO.allByDynatable(dynatableFilter, planCalificacion, idDepartamentoAcademico);
    }

    @Override
    @Transactional
    public void asignarCurso(Long idCurso, Long idPlanCalificacion, Long idUsuario) {
        Curso curso = cursoDAO.find(idCurso);
        curso.setPlanCalificacion(new PlanCalificacion(idPlanCalificacion));
        curso.setFechaPlanCalificacion(new Date());
        curso.setUserPlanCalificacion(idUsuario);
        cursoDAO.update(curso);
    }

    @Override
    @Transactional
    public void desasignarCurso(Long idCurso, Long idPlanCalificacion, Long idPersona) {
        Curso curso = cursoDAO.find(idCurso);
        curso.setPlanCalificacion(null);
        curso.setFechaPlanCalificacion(null);
        curso.setUserPlanCalificacion(idPersona);
        cursoDAO.update(curso);
    }

}
