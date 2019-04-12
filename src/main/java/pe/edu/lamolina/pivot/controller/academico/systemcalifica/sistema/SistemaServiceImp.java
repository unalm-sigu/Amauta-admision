package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.OrigenPlanCalificaEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Autowired
    PlanCalificacionCursoDAO planCalificacionCursoDAO;

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
//            jobj.put("esDivisible", tipoEvaluacion.getEsDivisible());
//            jobj.put("esNotaMinimaAnulable", tipoEvaluacion.isNotaMinimaAnulable());
//            jobj.put("cantidadMaxima", tipoEvaluacion.getCantidadMaxima());
            json.put(tipoEvaluacion.getId().toString(), jobj.toString());
        }
        return json;
    }

    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveSistemaCalifica(PlanCalificacion planCalificacion, DataSessionPivot ds) {

        DepartamentoAcademico departamento = buscarDepartamento(planCalificacion.getDepartamentoAcademico().getId(), ds);
        if (departamento == null) {
            throw new PhobosException("No tiene permiso para crear Planes de Calificación en este Departamento Académico");
        }

        planCalificacion.setDepartamentoAcademico(departamento);
        planCalificacion.setOrigenEnum(OrigenPlanCalificaEnum.DEP);
        planCalificacion.setUserRegistro(ds.getUsuario());

        planCalificacion.setEstadoEnum(EstadoPlanCalificaEnum.CRE);
        planCalificacion.setFechaRegistro(new Date());
//        planCalificacion.setTipo(TipoPlanCalificacionEnum.PLANT.name());
        planCalificacion.setTipoCiclo(ds.getCicloAcademico().getTipo());

        BigDecimal totalWeight = BigDecimal.ZERO;

        SistemaNotas sistemaNotas = sistemaNotasDAO.find(planCalificacion.getSistemaNotas().getId());
        planCalificacion.setSistemaNotas(sistemaNotas);

        if (planCalificacion.getSistemaNotas().isLetras()) {
            if (planCalificacion.getEvaluacionPlan().size() == 1) {
                TipoEvaluacion tipoEvaluacion = tipoEvaluacionDAO.find(planCalificacion.getEvaluacionPlan().get(0).getTipoEvaluacion().getId());
                if (!tipoEvaluacion.isTipoEvaluacionNF()) {
                    throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
                }
            } else {
                throw new PhobosException("El sistema de notas seleccionado, solo debe tener una evaluacion del tipo Nota Final.");
            }
        }

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
//            logger.debug("nota minima anulable {}", evaluacionPlan.getNotaMinimaAnulable());
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            evaluacionPlan.setCantidadEvaluaciones(BigDecimal.ONE.intValue());
            evaluacionPlan.setPesoEvaluacion(evaluacionPlan.getPesoTotal());
            /*
            if (evaluacionPlan.getPesoEvaluacion() == null || evaluacionPlan.getPesoEvaluacion().compareTo(BigDecimal.ZERO) == 0) {
                throw new PhobosException("Peso evaluacion incorrecto..");
            }
             */
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }

            totalWeight = totalWeight.add(evaluacionPlan.getPesoTotal());
        }

        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new PhobosException("Pesos total (" + totalWeight.toString() + ") de las evaluaciones incorrecto.");
        }

        Long maxNumeroCorrelativo = planCalificacionDAO.maxNumeroCorrelativoPlanCalifica(planCalificacion.getDepartamentoAcademico().getId());
        maxNumeroCorrelativo = maxNumeroCorrelativo + 1;
        planCalificacion.setNumero(maxNumeroCorrelativo);

        planCalificacion.generateCodigo();

        planCalificacionDAO.save(planCalificacion);
    }

    private List returnEmpty(DynatableFilter filter) {
        filter.setFiltered(0);
        filter.setTotal(0);
        return new ArrayList();
    }

    @Override
    public List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter filter, DataSessionPivot ds) {
        if (filter.getQueries() == null) {
            return returnEmpty(filter);
        }

        String dep = (String) filter.getQueries().get("departamento");
        if (StringUtils.isEmpty(dep)) {
            return returnEmpty(filter);
        }

        boolean tienePermiso = false;
        DepartamentoAcademico dpto = new DepartamentoAcademico(dep);
        List<DepartamentoAcademico> departamentos = ds.getDepartamentos();
        for (DepartamentoAcademico dd : departamentos) {
            tienePermiso = dd.getId().longValue() == dpto.getId();
            if (tienePermiso) {
                break;
            }
        }

        if (!tienePermiso) {
            return returnEmpty(filter);
        }

        List<PlanCalificacion> listaPlanes = planCalificacionDAO.allByDynatable(filter, dpto);
        List<Curso> cursos = cursoDAO.allByPlanes(listaPlanes);
        List<Curso> cursosRegulares = cursoDAO.allRegularesByPlanes(listaPlanes);
        //List<PlanCalificacionCurso> planesCursos = planCalificacionCursoDAO.allByFilter(plan, null, null, EstadoEnum.ACT);
        List<PlanCalificacionCurso> planesCursos = planCalificacionCursoDAO.allActivosByPLanes(listaPlanes);

        Map<Long, List<Curso>> mapCursos = TypesUtil.convertListToMapList("planCalificacion.id", cursos);
        Map<Long, List<Curso>> mapCursosReg = TypesUtil.convertListToMapList("planCalificacionRegular.id", cursosRegulares);
        Map<Long, List<PlanCalificacionCurso>> mapPlanesCurso = TypesUtil.convertListToMapList("planCalificacion.id", planesCursos);

        for (PlanCalificacion plan : listaPlanes) {
            plan.setPlanCalificacionCursos(TypesUtil.getListNotNull(mapPlanesCurso.get(plan.getId())));
            plan.setCurso(TypesUtil.getListNotNull(mapCursos.get(plan.getId())));
            plan.setCursosPlanRegular(TypesUtil.getListNotNull(mapCursosReg.get(plan.getId())));
        }
        return listaPlanes;
    }

    @Override
    public PlanCalificacion findPlanCalificacion(Long idPlanCalificacion) {
        PlanCalificacion plan = planCalificacionDAO.find(idPlanCalificacion);
        List<PlanCalificacionCurso> planCursos = planCalificacionCursoDAO.allByFilter(plan, null, null, EstadoEnum.ACT);
        List<EvaluacionPlan> evaluaciones = evaluacionPlanDAO.allByPlan(plan);

        plan.setPlanCalificacionCursos(planCursos);
        plan.setEvaluacionPlan(evaluaciones);
        return plan;
    }

    @Override
    @Transactional
    public void changeStatePlanCalificacion(
            Long idPLanCalificacion, String observacion, EstadoPlanCalificaEnum estadoPlanCalificaEnum, Usuario usuarioRegistro) {
        DateTime today = new DateTime();
        PlanCalificacion planCalificacion = planCalificacionDAO.find(idPLanCalificacion);
        planCalificacion.setEstadoEnum(estadoPlanCalificaEnum);
        planCalificacion.setObservacion(observacion);
        if (EstadoPlanCalificaEnum.ACEP.equals(estadoPlanCalificaEnum)) {
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(idPLanCalificacion, null, null);
            evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
            evaluacionSeccionDAO.update(evaluacionSeccion);

            GrupoSeccion grupoSeccion = grupoSeccionDAO.find(evaluacionSeccion.getGrupoSeccion().getId());
            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(planCalificacion);
            grupoSeccionDAO.update(grupoSeccion);

            this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, EstadoPlanCalificaEnum.ACEP, today.toDate(), usuarioRegistro);

            List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
            logger.debug("Cantidad de secciones para el grupo {}", secciones.size());
            List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
            logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", idPLanCalificacion, planEvaluaciones.size());
            for (Seccion seccionEach : secciones) {
                for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                    logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                    logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                    logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEvalEnum().name());
                    if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                            evaluacionExpandida.getTipoSeccionEvalEnum())) {

                        Evaluacion evaluacion = new Evaluacion();
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
                        evaluacionDAO.save(evaluacion);
                    }
                }
            }

        } else if (EstadoPlanCalificaEnum.RHZ.equals(estadoPlanCalificaEnum)
                || EstadoPlanCalificaEnum.OBS.equals(estadoPlanCalificaEnum)) {
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(idPLanCalificacion, null, null);
            evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
            evaluacionSeccionDAO.update(evaluacionSeccion);

            GrupoSeccion grupoSeccion = grupoSeccionDAO.find(evaluacionSeccion.getGrupoSeccion().getId());
            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(planCalificacion);
            grupoSeccionDAO.update(grupoSeccion);
        }
        planCalificacionDAO.update(planCalificacion);
    }

    private void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum, Date fechaRegistro, Usuario usuarioRegistro) {
        evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        List<EvaluacionExpandida> evaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null, null);
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
                    evaluacion.create(evaluacionSeccion, evaluacionPlan, i, fechaRegistro, usuarioRegistro);

                    if (i == evaluacionPlan.getCantidadEvaluaciones().intValue()) {
                        BigDecimal pesoFinal = evaluacionPlan.getPesoTotal().subtract(peso);
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
    public void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadoPlanCalificaEnum, Usuario usuarioRegistro) {
        changeStatePlanCalificacion(idPLanCalificacion, null, estadoPlanCalificaEnum, usuarioRegistro);
    }

    @Override
    public List<PlanCalificacionCurso> allPlanCalificacionCursosByFilterDyna(DynatableFilter dynatableFilter, PlanCalificacion planCalificacion) {
        return planCalificacionCursoDAO.allByFilterDyna(dynatableFilter, planCalificacion, EstadoEnum.ACT);
    }

    @Override
    @Transactional
    public void asignarCurso(Long idCurso, Long idPlanCalificacion, DataSessionPivot ds) {
        DateTime today = new DateTime();

        PlanCalificacion planCalificacion = planCalificacionDAO.find(idPlanCalificacion);
        Curso curso = cursoDAO.find(idCurso);

        logger.debug("plan calificacion {}, {}", planCalificacion.getId());
        logger.debug("curso {}, {}", curso.getId());

        DepartamentoAcademico dpto1 = curso.getDepartamentoAcademico();
        DepartamentoAcademico dpto2 = planCalificacion.getDepartamentoAcademico();
        if (dpto1.getId().longValue() != dpto2.getId()) {
            throw new PhobosException("El curso y el plan deben pertenecer al mismo Departamento Académico");
        }

        DepartamentoAcademico departamento = buscarDepartamento(dpto1.getId(), ds);
        if (departamento == null) {
            throw new PhobosException("No tiene permiso para incluir cursos en este Planes de Calificación");
        }

        /*
        if (planCalificacion.isTipoCicloNivelacion()) {
            curso.setPlanCalificacion(new PlanCalificacion(idPlanCalificacion));
        } else if (planCalificacion.isTipoCicloRegular()) {
            curso.setPlanCalificacionRegular(new PlanCalificacion(idPlanCalificacion));
        } else {
            throw new PhobosException("La aplicacion no cuenta con tipo de ciclo");
        }
        curso.setFechaPlanCalificacion(new Date());
        curso.setUserPlanCalificacion(idUsuario);
        cursoDAO.update(curso);
         */
        if (planCalificacion.getSistemaNotas().isLetras()) {
            if (!curso.isTieneCreditosVariables()) {
                if (curso.getCreditos() != null && curso.getCreditos().compareTo(BigDecimal.ZERO.intValue()) != 0) {
                    throw new PhobosException("Error, El curso debe tener creditos variables.");
                }
            }
        }
        if (curso.isTieneCreditosVariables()) {
            if (!planCalificacion.getSistemaNotas().isLetras()) {
                throw new PhobosException("Error, el sistema de notas debe ser de letras.");
            }
        }

        PlanCalificacionCurso planCalificacionCurso = planCalificacionCursoDAO.findByFilter(planCalificacion, curso, EstadoEnum.ACT);

        if (planCalificacionCurso != null) {
            throw new PhobosException("Error, El curso ya fué asignado al plan anteriormente.");
        }

        planCalificacionCurso = new PlanCalificacionCurso();
        planCalificacionCurso.setCurso(curso);
        planCalificacionCurso.setEstadoEnum(EstadoEnum.ACT);
        planCalificacionCurso.setFechaActualizacion(today.toDate());
        planCalificacionCurso.setFechaCreacion(today.toDate());
        planCalificacionCurso.setPlanCalificacion(planCalificacion);
        planCalificacionCursoDAO.save(planCalificacionCurso);
    }

    @Override
    @Transactional
    public void desasignarCurso(Long idPlanCurso, Long idPersona) {
        /*   Curso curso = cursoDAO.find(idCurso);
        curso.setPlanCalificacion(null);
        curso.setFechaPlanCalificacion(null);
        curso.setUserPlanCalificacion(idPersona);
        cursoDAO.update(curso);*/
        DateTime today = new DateTime();
        PlanCalificacionCurso planCalificacionCurso = planCalificacionCursoDAO.find(idPlanCurso);
        planCalificacionCurso.setEstadoEnum(EstadoEnum.INA);
        planCalificacionCurso.setFechaActualizacion(today.toDate());

        planCalificacionCursoDAO.update(planCalificacionCurso);
    }

    @Override
    public List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion) {
        return cursoDAO.allActiveByPlan(planCalificacion);
    }

    @Override
    public DepartamentoAcademico buscarDepartamento(Long idDepartamento, DataSessionPivot ds) {
        List<DepartamentoAcademico> departamentos = ds.getDepartamentos();
        for (DepartamentoAcademico departamento : departamentos) {
            if (departamento.getId().longValue() == idDepartamento) {
                return departamento;
            }
        }
        return null;
    }

}
