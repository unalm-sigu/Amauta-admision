package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
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
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ReclamoNotaDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import pe.edu.lamolina.pivot.model.academico.ResumenAlumnoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;

@Service
@Transactional(readOnly = true)
public class CargaAcademicaServiceImp implements CargaAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    PlanCalificacionDAO planCalificacionDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    TipoEvaluacionDAO tipoEvaluacionDAO;

    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    EvaluacionSeccionDAO evaluacionSeccionDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    SistemaNotasDAO sistemaNotasDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Autowired
    ReclamoNotaDAO reclamoNotaDAO;

    @Autowired
    ResumenAlumnoEvaluacionDAO resumenAlumnoEvaluacionDAO;

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico cicloAcademico) {
        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente);
        List<Long> lstIds = new ArrayList<>();
        for (DocenteSeccion docenteSeccion : docentesSecciones) {
            lstIds.add(docenteSeccion.getSeccion().getGrupoSeccion().getId());
            logger.debug("seccion {}, grupo {}", docenteSeccion.getSeccion().getId(), docenteSeccion.getSeccion().getGrupoSeccion().getId());
        }
        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(lstIds, cicloAcademico);

        return gruposSeccion;
    }

    @Override
    public List<TipoEvaluacion> allTipoEvaluacion() {
        return tipoEvaluacionDAO.all();
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo) {
        return docenteSeccionDAO.allByCargaAcademica(filter, docente, ciclo);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionByDocente(Docente docente) {
        return docenteSeccionDAO.allByDocente(docente);
    }

    @Override
    public PlanCalificacion findPlanCalificacion(Long idPlanCalificacion) {
        return planCalificacionDAO.find(idPlanCalificacion);
    }

    @Override
    public DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion) {
        return docenteSeccionDAO.findByFilter(docente, seccion);
    }

    @Override
    public Curso findCurso(Long idCurso) {
        return cursoDAO.find(idCurso);
    }

    @Override
    public Seccion findSeccion(Long idSeccion) {
        return seccionDAO.find(idSeccion);
    }

    @Override
    public GrupoSeccion findGrupo(Long idGrupoSeccion) {
        GrupoSeccion gpoSecc = grupoSeccionDAO.find(idGrupoSeccion);
        List<EvaluacionSeccion> evalSeccs = evaluacionSeccionDAO.allByGrupoSeccion(gpoSecc);
        gpoSecc.setEvaluacionSecciones(evalSeccs);

        return gpoSecc;
    }

    @Override
    public List<EvaluacionPlan> allEvaluacionPlanByDynatable(DynatableFilter filter, Long idPlanCalificacion) {
        return evaluacionPlanDAO.allByDynatable(filter, idPlanCalificacion);
    }

    @Override
    public List<EvaluacionPlan> allEvaluacionPlanByPlanCalifica(Long idPlanCalificacion) {
        return evaluacionPlanDAO.allByFilter(idPlanCalificacion);
    }

    @Override
    public EvaluacionPlan findEvaluacionPlan(Long idEvaluacionPlan) {
        return evaluacionPlanDAO.find(idEvaluacionPlan);
    }

    @Override
    public Evaluacion findEvaluacion(Long idEvaluacion) {
        return evaluacionDAO.find(idEvaluacion);
    }

    @Override
    @Transactional
    public void createEvaluacionSeccionPorDocente(Docente docente) {

        List<DocenteSeccion> lstDocenteSeccion = docenteSeccionDAO.allByDocente(docente);
        logger.debug("Lista de secciones por docente {}", lstDocenteSeccion.size());
        for (DocenteSeccion docenteSeccion : lstDocenteSeccion) {

            GrupoSeccion grupoSeccion = docenteSeccion.getSeccion().getGrupoSeccion();
            Curso curso = docenteSeccion.getSeccion().getGrupoSeccion().getCurso();

            if (ObjectUtil.getParentTree(curso, "planCalificacion.id") == null) {
                logger.debug("el curso {} no cuenta con plan calificacion", curso.getId());
                continue;
            }

            Long idGrupoSeccion = grupoSeccion.getId();
            Long idPlanCalificacion = curso.getPlanCalificacion().getId();
            logger.debug("Grupo seccion {}, plan calificacion {}", idGrupoSeccion, idPlanCalificacion);
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, idGrupoSeccion);
            PlanCalificacion planCalificacion = planCalificacionDAO.find(idPlanCalificacion);

            if (evaluacionSeccion != null) {
                logger.debug("el grupo ya cuenta con evaluacion seccion");
                if (evaluacionSeccion.isEstadoPro()) {
                    evaluacionSeccion.setPlanCalificacion(planCalificacion);
                    evaluacionSeccion.setSistemaNotas(planCalificacion.getSistemaNotas());
                    evaluacionSeccion.setGrupoSeccion(new GrupoSeccion(idGrupoSeccion));
                    evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
                    evaluacionSeccionDAO.update(evaluacionSeccion);

                    grupoSeccion.setPlanCalificacion(planCalificacion);
                    grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
                    grupoSeccionDAO.update(grupoSeccion);
                }
            } else {

                logger.debug("se le creara una evaluacion seccion al grupo");
                EvaluacionSeccion evaluacionSeccionCreate = new EvaluacionSeccion();
                evaluacionSeccionCreate.setPlanCalificacion(planCalificacion);
                evaluacionSeccionCreate.setSistemaNotas(planCalificacion.getSistemaNotas());
                evaluacionSeccionCreate.setGrupoSeccion(new GrupoSeccion(idGrupoSeccion));
                evaluacionSeccionCreate.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
                evaluacionSeccionDAO.save(evaluacionSeccionCreate);

                grupoSeccion.setPlanCalificacion(planCalificacion);
                grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
                grupoSeccionDAO.update(grupoSeccion);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createEvaluacionExpPorEvalSeccion(EvaluacionSeccion evaluacionSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        evaluacionSeccion.setEstadoEnum(estadoPlanCalificaEnum);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        List<EvaluacionExpandida> evaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Evaluacion seccion {}, cantidad de pensiones expandidadas {}", evaluacionSeccion.getId(), evaluaciones.size());
        if (evaluaciones.isEmpty()) {
            logger.debug("no tiene evaluaciones, se creara las evaluaciones en base al plan calificacion {}", evaluacionSeccion.getPlanCalificacion().getId());

            List<EvaluacionPlan> evaluacionesPlanes = this.allEvaluacionPlanByPlanCalifica(evaluacionSeccion.getPlanCalificacion().getId());
            logger.debug("Plan Calificacion {}, Cantidad de evaluaciones para el plan {} ", evaluacionSeccion.getPlanCalificacion().getId(), evaluacionesPlanes.size());

            for (EvaluacionPlan evaluacionPlan : evaluacionesPlanes) {

                BigDecimal peso = BigDecimal.ZERO;
                for (int i = 1; i <= evaluacionPlan.getCantidadEvaluaciones(); i++) {
                    EvaluacionExpandida evaluacion = new EvaluacionExpandida();
                    evaluacion.setAlumnoEvaluacion(null);
                    evaluacion.create(evaluacionSeccion, evaluacionPlan, i);

                    if (i == evaluacionPlan.getCantidadEvaluaciones()) {
                        BigDecimal pesoFinal = evaluacionPlan.getPesoTotal().subtract(peso);
                        evaluacion.setPeso(pesoFinal);
                    }
                    peso = peso.add(evaluacionPlan.getPesoEvaluacion());
                    evaluacionExpandidaDAO.save(evaluacion);
                }
            }

            GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
            grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
            grupoSeccion.setPlanCalificacion(evaluacionSeccion.getPlanCalificacion());
            grupoSeccionDAO.update(grupoSeccion);

            this.aceptarExpansion(evaluacionSeccion.getId(), null);
        }

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(estadoPlanCalificaEnum);
        grupoSeccion.setPlanCalificacion(evaluacionSeccion.getPlanCalificacion());
        grupoSeccionDAO.update(grupoSeccion);
    }

    @Override
    @Transactional
    public void saveExpansionEvaluacion(EvaluacionExpandida evaluacion, DataSessionPivot ds) {
        logger.debug("La evaluacion expandida padre es {}", evaluacion.getId());

        EvaluacionExpandida evaluacionPadre = evaluacionExpandidaDAO.find(evaluacion.getId());
        logger.debug("Evaluacion Exp {}, Cantidad de Hijos {}", evaluacionPadre.getId(), evaluacionPadre.getEvaluacionesExpandidas().size());

        evaluacionPadre.setEstaDesagregado(BigDecimal.ONE.intValue());
        evaluacionPadre.setFechaDesagregar(new Date());
        evaluacionPadre.setUsuarioDesagregar(ds.getUsuario());

        BigDecimal newPesoTotal = BigDecimal.ZERO;

        for (EvaluacionExpandida evaChilds : evaluacionPadre.getEvaluacionesExpandidas()) {
            if (!evaChilds.isNotasIngresadas()) {
                evaluacionDAO.deleteByEvaluacionExpandida(evaChilds.getId());
            }
        }
        evaluacionExpandidaDAO.deleteByEvaluacionParent(evaluacionPadre.getId());

        //Evaluaciones que actualmente se expanderan
        for (EvaluacionExpandida evaluacionHija : evaluacion.getEvaluacionesExpandidas()) {
            newPesoTotal = newPesoTotal.add(evaluacionHija.getPeso());
        }
        logger.debug("new peso total {}, eva padre peso {}", newPesoTotal, evaluacionPadre.getPeso());
        if (newPesoTotal.compareTo(evaluacionPadre.getPeso()) != 0) {
            throw new PhobosException("El peso de las evaluaciones expandidas debe ser igual al peso de la evaluacion padre, verifique ");
        }
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.find(evaluacion.getEvaluacionSeccion().getId());

        List<Seccion> secciones = seccionDAO.allByFilter(evaluacionSeccion.getGrupoSeccion().getId());
        logger.debug("la cantidad de secciones para el grupo {}, es {}", evaluacionSeccion.getGrupoSeccion().getId(), secciones.size());

        //  int numero = 1;
        //  StringBuilder strb = new StringBuilder();
        for (EvaluacionExpandida evaluacionHija : evaluacion.getEvaluacionesExpandidas()) {
            if (evaluacionHija.isNotasIngresadas()) {
                logger.debug("Evaluacion {}, tiene notas ingresadas", evaluacionHija.getId());
                continue;
            }
            /*
            StringBuilder strbFilter = new StringBuilder();
            strbFilter.append(",").append(evaluacionHija.getTipoEvaluacion().getId()).append(",");
             */
 /*
            if (strb.toString().contains(strbFilter.toString())) {
                throw new PhobosException("Las evaluaciones no se pueden repetir, verifique.");
            }
             */
            //    if (evaluacionHija.getId() == null) {
            evaluacionHija.setId(null);
            evaluacionHija.setAlumnoEvaluacion(null);
            evaluacionHija.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evaluacionHija.setEvaluacionSeccion(evaluacionPadre.getEvaluacionSeccion());
            evaluacionHija.setEvaluacionSuperior(evaluacionPadre);
            evaluacionHija.setEvaluacionesExpandidas(null);
            evaluacionHija.setEvaluados(BigDecimal.ZERO.intValue());
            evaluacionHija.setExtemporaneos(BigDecimal.ZERO.intValue());
            evaluacionHija.setFechaDesagregar(null);
            evaluacionHija.setPeso(evaluacionHija.getPeso());
            evaluacionHija.setTipoEvaluacion(evaluacionHija.getTipoEvaluacion());
            evaluacionHija.setTipoSeccion(evaluacionPadre.getTipoSeccion());
            evaluacionHija.setUsuarioDesagregar(null);
            //  evaluacionHija.setNumero(numero);
            evaluacionHija.getEvaluacionSeccion().getGrupoSeccion();
            evaluacionHija.setIndPorcentajeVariable(evaluacionPadre.getIndPorcentajeVariable());

            Date today = new Date();
            evaluacionHija.setEvaluaciones(new ArrayList<>());
            for (Seccion seccionEach : secciones) {
                Evaluacion evalPadreBySeccion = evaluacionDAO.findByEvalExpSeccion(evaluacionPadre.getId(), seccionEach.getId());

                if (evalPadreBySeccion != null) {
                    if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                            evaluacionHija.getTipoSeccionEnum())) {
                        if (!evalPadreBySeccion.isDesagregado()) {
                            evalPadreBySeccion.setEstaDesagregado(BigDecimal.ONE.intValue());
                            evalPadreBySeccion.setUsuarioDesagregar(ds.getUsuario());
                            evalPadreBySeccion.setFechaDesagregar(today);
                            evaluacionDAO.update(evalPadreBySeccion);
                        }
                        Evaluacion evaluacionChild = new Evaluacion();
                        evaluacionChild.create(evaluacionSeccion, seccionEach, evaluacionHija);
                        evaluacionChild.setEvaluacionSuperior(evalPadreBySeccion);
                        evaluacionChild.setEstaDesagregado(BigDecimal.ZERO.intValue());
                        evaluacionHija.getEvaluaciones().add(evaluacionChild);

                    }
                }
            }
            if (evaluacionHija.getEvaluaciones().isEmpty()) {
                evaluacionHija.setEvaluaciones(null);
            }

            //  numero++;
            evaluacionExpandidaDAO.save(evaluacionHija);

            /*   } else {
                EvaluacionExpandida evalExpandidaHija = evaluacionExpandidaDAO.find(evaluacionHija.getId());
                evalExpandidaHija.setTipoEvaluacion(evaluacionHija.getTipoEvaluacion());
                evalExpandidaHija.setPeso(evaluacionHija.getPeso());
                evaluacionExpandidaDAO.update(evaluacionHija);

            }*/
 /*
            strb.append(",");
            strb.append(evaluacionHija.getTipoEvaluacion().getId());
            strb.append(",");*/
        }
        /*
        logger.debug("222222222222222222222222222");
        EvaluacionExpandida evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionPadre.getId());
        logger.debug("Evaluacion Exp {}, Cantidad de Hijos {}", evaluacionPadre.getId(), evaluacionExpandida.getEvaluacionesExpandidas().size());

        for (Seccion seccionEach : secciones) {

            Evaluacion evalPadreBySeccion = evaluacionDAO.findByEvalExpSeccion(evaluacionExpandida.getId(), seccionEach.getId());
            if (evalPadreBySeccion != null) {

                if (evaluacion.getEvaluacionesExpandidas() != null && !evaluacion.getEvaluacionesExpandidas().isEmpty()) {
                    for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                        logger.debug("222");
                        Evaluacion evaluacionChild = new Evaluacion();
                        evaluacionChild.create(evaluacionSeccion, seccionEach, evalExp);
                        evaluacionChild.setEvaluacionSuperior(evalPadreBySeccion);
                        evaluacionChild.setEstaDesagregado(BigDecimal.ZERO.intValue());
                        evaluacionDAO.save(evaluacionChild);
                        logger.debug("333");
                    }
                }
            }

        }
         */
        evaluacionExpandidaDAO.update(evaluacionPadre);
    }

    @Override
    @Transactional
    public void saveAsignacionDocentes(EvaluacionExpandida evaluacionExpandida, DataSessionPivot ds) {
        for (Evaluacion evaluacion : evaluacionExpandida.getEvaluaciones()) {
            if (!evaluacion.isNotasIngresadas()) {
                evaluacionDAO.updateDocenteEvaluador(evaluacion, evaluacion.getDocenteEvaluador());
            }
        }
    }

    @Override
    @Transactional
    public void saveSistemaCalifica(PlanCalificacion planCalificacion, Long grupoSeccionId) {

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(grupoSeccionId);
        logger.debug("Grupo Seccion Id {}", grupoSeccion.getId());

        DepartamentoAcademico departamentoAcademico = departamentoAcademicoDAO.find(planCalificacion.getDepartamentoAcademico().getId());

        planCalificacion.setEstadoEnum(EstadoPlanCalificaEnum.SOL);
        planCalificacion.setFechaRegistro(new Date());
        planCalificacion.setDepartamentoAcademico(departamentoAcademico);

        BigDecimal totalWeight = BigDecimal.ZERO;

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            if (evaluacionPlan.getPesoEvaluacion() == null || evaluacionPlan.getPesoEvaluacion().compareTo(BigDecimal.ZERO) == 0) {
                throw new PhobosException("Peso evaluacion incorrecto..");
            }
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
        /*
        EvaluacionSeccion evaluacionSeccion = new EvaluacionSeccion();
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.SOL);
        evaluacionSeccion.setGrupoSeccion(grupoSeccion);
        evaluacionSeccion.setPlanCalificacion(planCalificacion);
        evaluacionSeccion.setEvaluaciones(new ArrayList<Evaluacion>());

        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {

            Evaluacion evaluacion = new Evaluacion();
            evaluacion.setEvaluacionSeccion(evaluacionSeccion);
            evaluacion.setAlumnoEvaluacion(null);
            evaluacion.setEvaluacionSeccion(evaluacionSeccion);
            evaluacion.setTipoEvaluacion(evaluacionPlan.getTipoEvaluacion());
            evaluacion.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evaluacion.setEvaluacionSuperior(null);
            evaluacion.setEvaluaciones(null);
            evaluacion.setEvaluados(BigDecimal.ZERO.intValue());
            evaluacion.setPeso(evaluacionPlan.getPesoTotal());
            evaluacionSeccion.getEvaluaciones().add(evaluacion);
        }
        evaluacionSeccionDAO.save(evaluacionSeccion);
         */
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.SOL);
        grupoSeccion.setPlanCalificacion(planCalificacion);
        grupoSeccionDAO.update(grupoSeccion);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupoSeccion.getId());
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.SOL);
        evaluacionSeccion.setPlanCalificacion(planCalificacion);
        evaluacionSeccionDAO.update(evaluacionSeccion);

    }

    @Override
    public EvaluacionSeccion findEvalSeccByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion) {
        return evaluacionSeccionDAO.findByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
    }

    @Override
    public EvaluacionSeccion findEvaluacionSeccion(Long id) {
        return evaluacionSeccionDAO.find(id);
    }

    @Override
    public List<Evaluacion> allEvaluacionesByEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        return evaluacionDAO.allByFilter(evaluacionSeccion.getId(), null, null, null);
    }

    @Override
    public List<Evaluacion> allEvaluacionesByEvalExpandida(EvaluacionExpandida evaluacionExpandida) {
        return evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
    }

    @Override
    public List<EvaluacionExpandida> allEvaluacionesExpByEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        Map<Long, EvaluacionExpandida> mapEvaluacionesExp = new LinkedHashMap();
        Map<Long, EvaluacionExpandida> mapEvaluacionesExpHijas = new LinkedHashMap();
        List<EvaluacionExpandida> evaluacionesExp = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        for (EvaluacionExpandida evalExp : evaluacionesExp) {
            evalExp.setEvaluaciones(new ArrayList());
            mapEvaluacionesExp.put(evalExp.getId(), evalExp);

            List<EvaluacionExpandida> evalExpansHijas = evalExp.getEvaluacionesExpandidas();
            for (EvaluacionExpandida evalExpHija : evalExpansHijas) {
                evalExpHija.setEvaluaciones(new ArrayList());
                mapEvaluacionesExpHijas.put(evalExpHija.getId(), evalExpHija);
            }
        }

        List<EvaluacionExpandida> evalsExpsQuery = new ArrayList();
        evalsExpsQuery.addAll(mapEvaluacionesExpHijas.values());
        evalsExpsQuery.addAll(evaluacionesExp);

        List<Evaluacion> evals = evaluacionDAO.allByEvaluacionesExpandidas(evalsExpsQuery);
        for (Evaluacion eval : evals) {
            EvaluacionExpandida evalExp = mapEvaluacionesExp.get(eval.getEvaluacionExpandida().getId());
            if (evalExp == null) {
                evalExp = mapEvaluacionesExpHijas.get(eval.getEvaluacionExpandida().getId());
            }
            evalExp.getEvaluaciones().add(eval);
        }

        return evaluacionesExp;
    }

    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }

    @Override
    @Transactional
    public void aceptarRechazo(Long cursoId, Long grupoId, DataSessionPivot ds) {
        logger.debug("CursoId {}, GrupoId {}", cursoId, grupoId);

        Curso curso = cursoDAO.find(cursoId);
        GrupoSeccion grupo = grupoSeccionDAO.find(grupoId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupo.getId());
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
        evaluacionSeccion.setPlanCalificacion(curso.getPlanCalificacion());
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
        grupoSeccion.setPlanCalificacion(curso.getPlanCalificacion());
        grupoSeccionDAO.update(grupoSeccion);

    }

    @Override
    @Transactional
    public void aceptarPlanCalificacion(Long cursoId, Long grupoId, DataSessionPivot ds) {
        logger.debug("CursoId {}, grupoId {}", cursoId, grupoId);

        Curso curso = cursoDAO.find(cursoId);
        GrupoSeccion grupo = grupoSeccionDAO.find(grupoId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, grupo.getId());
        logger.debug("La evaluacion seccion es {}", evaluacionSeccion.getId());
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, EstadoPlanCalificaEnum.ACEP);

    }

    @Override
    @Transactional
    public void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds) {
        logger.debug("La evaluacionSeccionId es {}", evaluacionSeccionId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccionId);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.EXPR);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.EXPR);
        grupoSeccionDAO.update(grupoSeccion);

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("la cantidad de secciones para el grupo {}, es {}", grupoSeccion.getId(), secciones.size());
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", grupoSeccion.getPlanCalificacion().getId(), planEvaluaciones.size());

        for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
            BigDecimal pesoTotal = evaluacionExpandida.getPeso();
            BigDecimal pesoAcum = BigDecimal.ZERO;
            if (evaluacionExpandida.getEvaluacionesExpandidas() != null && !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty()) {

                for (EvaluacionExpandida evalExp : evaluacionExpandida.getEvaluacionesExpandidas()) {
                    pesoAcum = pesoAcum.add(evalExp.getPeso());
                }

                if (pesoTotal.compareTo(pesoAcum) != 0) {
                    String msg = "Pesos de las subevaluaciones de la evaluación {1} {2} incorrectos, verifique";
                    msg = msg.replace("{1}", evaluacionExpandida.getTipoEvaluacion().getNombre());
                    msg = msg.replace("{2}", evaluacionExpandida.getNumero().toString());
                    throw new PhobosException(msg);
                }
            }
        }
        for (Seccion seccionEach : secciones) {
            for (EvaluacionExpandida evaluacionExpandida : planEvaluaciones) {
                logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
                logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
                logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEnum().name());
                if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                        evaluacionExpandida.getTipoSeccionEnum())) {

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

    }

    @Override
    public DocenteSeccion findDocenteSeccion(Long idDocenteSeccion) {
        return docenteSeccionDAO.find(idDocenteSeccion);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion) {
        return docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
    }

    @Override
    public List<Evaluacion> allEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        return evaluacionDAO.allByFilter(null, idGrupoSeccion, null, null);
    }

    @Override
    public List<AlumnoEvaluacion> allAlumnoEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        return alumnoEvaluacionDAO.allByFilter(null, idGrupoSeccion, null);
    }

    @Override
    public List<Evaluacion> findBySeccion(Long idSeccion) {
        return evaluacionDAO.allByFilter(null, null, idSeccion, null);
    }

    @Override
    public EvaluacionExpandida findEvaluacionExpandida(Long idEvaluacionPlan) {
        return evaluacionExpandidaDAO.find(idEvaluacionPlan);
    }

    @Override
    @Transactional
    public void deleteEvaluacionExpandida(Long id) {

        EvaluacionExpandida evaluacion = evaluacionExpandidaDAO.find(id);
        EvaluacionExpandida evalSuperior = evaluacion.getEvaluacionSuperior();
        logger.debug("Evaluacion expandida a eliminar {}, Evaluacion Padre {}", id, evalSuperior.getId());
        evaluacionExpandidaDAO.delete(evaluacion);

        if (evalSuperior != null) {
            evalSuperior = evaluacionExpandidaDAO.find(evalSuperior.getId());
            if (evalSuperior.getEvaluacionesExpandidas() == null || evalSuperior.getEvaluacionesExpandidas().isEmpty()) {
                evalSuperior.setEstaDesagregado(BigDecimal.ZERO.intValue());
                evaluacionExpandidaDAO.update(evalSuperior);
            } else {
                logger.debug("Cantidad de evaluaciones hijas del padre {}", evalSuperior.getEvaluacionesExpandidas().size());
                if (evalSuperior.getEvaluacionesExpandidas().size() == 1) {
                    for (EvaluacionExpandida eva : evalSuperior.getEvaluacionesExpandidas()) {
                        if (eva.getId().equals(id)) {
                            evalSuperior.setEstaDesagregado(BigDecimal.ZERO.intValue());
                            evaluacionExpandidaDAO.update(evalSuperior);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion) {
        return matriculaSeccionDAO.allBySeccion(seccion);
    }

    @Override
    @Transactional
    public void updateEvaluacion(Evaluacion evaluacion) {
        evaluacionDAO.update(evaluacion);
    }

    @Override
    @Transactional
    public Evaluacion activarEvaluacion(Long evaluacionId, Date fechaRealizada, DataSessionPivot ds) {
        Evaluacion evaluacion = evaluacionDAO.find(evaluacionId);
        logger.debug("evaluacion param {}, {}", evaluacionId, evaluacion == null ? "no encontro" : "si encontro");

        if (evaluacion.getDocenteEvaluador() == null) {
            throw new PhobosException("La evaluación no cuenta con evaluador, verifique");
        }
        if (!evaluacion.getDocenteEvaluador().getId().equals(ds.getDocente().getId())) {
            throw new PhobosException("Docente evaluador incorrecto, verifique");
        }

        evaluacion.setFechaRealizada(fechaRealizada);
        evaluacionDAO.update(evaluacion);
        return evaluacion;
    }

    @Override
    @Transactional
    public void saveIngresoNotas(DataSessionPivot ds, Evaluacion evaluacionParam, AlumnoEvaluacion[] alumnosEvaluaciones) {
        Date today = new Date();

        Evaluacion evaluacion = evaluacionDAO.find(evaluacionParam.getId());

        EvaluacionExpandida evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacion.getEvaluacionExpandida().getId());
        evaluacionExpandida.setIndNotasIngresadas(BigDecimal.ONE.intValue());
        evaluacionExpandidaDAO.update(evaluacionExpandida);

        PlanCalificacion planCalificacion = evaluacion.getEvaluacionSeccion().getPlanCalificacion();
        CicloAcademico ciclo = evaluacion.getSeccionResponsable().getGrupoSeccion().getCicloAcademico();
        SistemaNotas sistemaNotas = sistemaNotasDAO.find(evaluacion.getEvaluacionSeccion().getSistemaNotas().getId());

        Seccion seccion = seccionDAO.find(evaluacion.getSeccionResponsable().getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();

        evaluacion.setFechaIngresoNota(today);
        evaluacion.setEvaluados(alumnosEvaluaciones.length);
        evaluacionDAO.update(evaluacion);

        Map<Long, Alumno> mapAlumno = new LinkedHashMap();

        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            Alumno alumnoEach = alumnoEvaluacionEach.getAlumno();
            Evaluacion evaluacionEach = alumnoEvaluacionEach.getEvaluacion();

            AlumnoEvaluacion alumnoEvaluacion = new AlumnoEvaluacion();
            alumnoEvaluacion.setAlumno(alumnoEach);
            alumnoEvaluacion.setEvaluacion(evaluacionEach);
            alumnoEvaluacion.setFechaIngresoNota(today);
            alumnoEvaluacion.setNota(alumnoEvaluacionEach.getNota());
            alumnoEvaluacion.setEsIngresoRegular(BigDecimal.ONE.intValue());
            mapAlumno.put(alumnoEach.getId(), alumnoEach);

            if (alumnoEvaluacion.getNota().equals(AlumnoEvaluacion.NSP)) {
                alumnoEvaluacion.setValorNumerico(BigDecimal.ZERO);
            } else if (sistemaNotas.isNumerico()) {
                alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
                String notax = NumberFormat.notaDecimal(alumnoEvaluacion.getValorNumerico());
                alumnoEvaluacion.setNota(notax);
            } else {
                NotaLetra notaLetra = sistemaNotas.getNotaLetra(alumnoEvaluacion.getNota());
                alumnoEvaluacion.setValorNumerico(new BigDecimal(notaLetra.getValor()));
            }

            alumnoEvaluacion.setUsuarioIngresoNota(ds.getUsuario());
            alumnoEvaluacion.setEstado("");
            alumnoEvaluacionDAO.save(alumnoEvaluacion);
        }

        List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByPlan(planCalificacion);

        BigDecimal bd100 = new BigDecimal("100");
        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            Alumno alumno = alumnoEvaluacionEach.getAlumno();
            GrupoSeccion gpoSeccion = evaluacion.getSeccionResponsable().getGrupoSeccion();
            Curso curso = gpoSeccion.getCurso();
            List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, ciclo);

            calcularNotasAlumno(alumno, evaluacion, grupoSeccion, curso, ciclo, evaluacionesPlan);
        }

        if (evaluacionDAO.countEvaluacionesFaltantesByGrupo(grupoSeccion.getId()).intValue() == 0) {
            grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.CER);
            grupoSeccionDAO.update(seccion.getGrupoSeccion());
        }
    }

    public void calcularNotasAlumno(Alumno alumno, Evaluacion evaluacion,
            GrupoSeccion grupoSeccion, Curso curso,
            CicloAcademico ciclo, List<EvaluacionPlan> evaluacionesPlan) {

        BigDecimal bd100 = new BigDecimal("100");
        List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, ciclo);
        MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);

        BigDecimal pesoTotal = BigDecimal.ZERO;
        BigDecimal ponderado = BigDecimal.ZERO;
        for (AlumnoEvaluacion ae : evaluacionesAlumno) {
            BigDecimal peso = choiceEvaluacion(ae.getEvaluacion(), evaluacion).getPeso();
            pesoTotal = pesoTotal.add(peso);
            ponderado = ponderado.add(peso.multiply(ae.getValorNumerico()));
        }

        BigDecimal avance = ponderado.divide(bd100, 2, RoundingMode.HALF_UP);
        BigDecimal prom = ponderado.divide(pesoTotal, 2, RoundingMode.HALF_UP);
        matriculaCurso.setNotaAvance(NumberFormat.notaDecimal(prom));
        matriculaCurso.setNotaAcumulada(NumberFormat.notaDecimal(avance));
        matriculaCurso.setPorcentajeAvanceNota(pesoTotal.intValue());

        if (pesoTotal.compareTo(bd100) == 0) {
            //sBigDecimal notaFinal = ponderado.divide(bd100, 0, RoundingMode.HALF_UP);
            BigDecimal notaFinal = calularNota(ponderado, bd100, 0);
            matriculaCurso.setNotaFinal(NumberFormat.nota(notaFinal));
        }
        matriculaCursoDAO.update(matriculaCurso);

        Map<Long, ResumenAlumnoEvaluacion> mapResumenAluEval = new LinkedHashMap();

        List<ResumenAlumnoEvaluacion> resumenTipoEVal = resumenAlumnoEvaluacionDAO.allByAlumnoGrupoSeccion(alumno, grupoSeccion);
        for (ResumenAlumnoEvaluacion rae : resumenTipoEVal) {
            mapResumenAluEval.put(rae.getTipoEvaluacion().getId(), rae);
        }

        pesoTotal = BigDecimal.ZERO;
        ponderado = BigDecimal.ZERO;
        for (EvaluacionPlan ep : evaluacionesPlan) {
            TipoEvaluacion tipo = ep.getTipoEvaluacion();
            List<AlumnoEvaluacion> evalsTipo = allEvaluacionesByTipoEvaluacion(tipo, evaluacionesAlumno, evaluacion);
            if (evalsTipo.isEmpty()) {
                continue;
            }
            ResumenAlumnoEvaluacion rae = mapResumenAluEval.get(tipo.getId());
            if (rae == null) {
                rae = new ResumenAlumnoEvaluacion();
                rae.setAlumno(alumno);
                rae.setGrupoSeccion(grupoSeccion);
                rae.setTipoEvaluacion(tipo);
            }
            rae.setEvaluaciones(evalsTipo.size());

            for (AlumnoEvaluacion ae : evalsTipo) {
                BigDecimal peso = choiceEvaluacion(ae.getEvaluacion(), evaluacion).getPeso();
                pesoTotal = pesoTotal.add(peso);
                ponderado = ponderado.add(peso.multiply(ae.getValorNumerico()));
            }

            BigDecimal nota = calularNota(ponderado, pesoTotal, 2);
            rae.setNota(NumberFormat.notaDecimal(nota));
            if (rae.getId() == null) {
                resumenAlumnoEvaluacionDAO.save(rae);
            } else {
                resumenAlumnoEvaluacionDAO.update(rae);
            }

        }
    }

    private BigDecimal calularNota(BigDecimal ponderado, BigDecimal pesoTotal, int redondeo) {
        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal nota = ponderado.divide(pesoTotal, redondeo, RoundingMode.HALF_UP);
        return nota;
    }

    private List<AlumnoEvaluacion> allEvaluacionesByTipoEvaluacion(TipoEvaluacion tipo, List<AlumnoEvaluacion> evaluacionesAlumno, Evaluacion evaluacion) {
        List<AlumnoEvaluacion> evalsTipo = new ArrayList();
        for (AlumnoEvaluacion aluEval : evaluacionesAlumno) {
            Evaluacion eval = choiceEvaluacion(aluEval.getEvaluacion(), evaluacion);

            if (eval.getEvaluacionSuperior() != null) {
                Evaluacion evalSup = choiceEvaluacion(eval.getEvaluacionSuperior(), evaluacion);

                TipoEvaluacion tipoEvalSup = evalSup.getTipoEvaluacion();

                if (tipoEvalSup.getId().equals(tipo.getId())) {
                    evalsTipo.add(aluEval);
                    continue;
                }
            }
            TipoEvaluacion tipoEval = eval.getTipoEvaluacion();

            if (tipoEval.getId().equals(tipo.getId())) {
                evalsTipo.add(aluEval);
            }
        }
        return evalsTipo;
    }

    private Evaluacion choiceEvaluacion(Evaluacion evaluacion, Evaluacion evaluacionMain) {
        if (evaluacion.getId().longValue() == evaluacionMain.getId()) {
            return evaluacionMain;
        }
        return evaluacion;
    }

    @Override
    public SistemaNotas findSistemaNotaById(Long id) {
        return sistemaNotasDAO.find(id);
    }

    @Override
    public ObjectNode getDetalleEvaluacion(Long idEvaluacion, Long idSeccion) {
        Evaluacion evaluacion = this.findEvaluacion(idEvaluacion);
        logger.debug("evaluacion param {}, {}", idEvaluacion, evaluacion == null ? "no encontro" : "si encontro");

        Seccion seccion = seccionDAO.find(idSeccion);
        GrupoSeccion grupoSeccion = this.findGrupo(seccion.getGrupoSeccion().getId());
        List<AlumnoEvaluacion> alumnosEvaluaciones = this.allAlumnoEvaluacionByFilter(null, null, seccion.getId());

        EvaluacionSeccion evaluacionSeccion = this.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId());
        SistemaNotas sistemaNotas = evaluacionSeccion.getSistemaNotas();

        BigDecimal notaminima = BigDecimal.valueOf(1000L);
        BigDecimal notaMaxima = BigDecimal.ZERO;
        BigDecimal sumatoriaNotas = BigDecimal.ZERO;
        int cantidadNsp = 0;
        int cantidadEvaluados = 0;

        for (AlumnoEvaluacion alumnosEvaluacionEach : alumnosEvaluaciones) {
            if (!alumnosEvaluacionEach.getEvaluacion().getId().equals(evaluacion.getId())) {
                continue;
            }
            //  if (sistemaNotas.isNumerico()) {
            if (alumnosEvaluacionEach.getValorNumerico().compareTo(notaminima) < 0) {
                notaminima = alumnosEvaluacionEach.getValorNumerico();
            }
            if (alumnosEvaluacionEach.getValorNumerico().compareTo(notaMaxima) > 0) {
                notaMaxima = alumnosEvaluacionEach.getValorNumerico();
            }
            if (alumnosEvaluacionEach.getNota().equalsIgnoreCase(AlumnoEvaluacion.NSP)) {
                cantidadNsp++;
            } else {
                cantidadEvaluados++;
                sumatoriaNotas = sumatoriaNotas.add(alumnosEvaluacionEach.getValorNumerico());
            }

            //  }
        }

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        if (evaluacion != null) {
            node.put("evaluacionId", evaluacion.getId());
            node.put("estado", evaluacion.getFechaIngresoNota() == null ? "CERRADA" : "ABIERTA");
            node.put("tEvaluacionNombre", evaluacion.getTipoEvaluacion().getNombre());
            node.put("tEvaluacionCodigo", evaluacion.getTipoEvaluacion().getCodigo());
            node.put("numero", evaluacion.getNumero());
            node.put("evaFechaIngresoNota", evaluacion.getFechaIngresoNota() != null ? new DateTime(evaluacion.getFechaIngresoNota()).toString("dd/MM/yyyy") : "");
            node.put("evaFechaRealizada", evaluacion.getFechaRealizada() != null ? new DateTime(evaluacion.getFechaRealizada()).toString("dd/MM/yyyy") : "");
            node.put("notaminima", 0);
            if (BigDecimal.valueOf(1000L).compareTo(notaminima) != 0) {
                node.put("notaminima", notaminima);
            }

            node.put("notaMaxima", notaMaxima);
            node.put("cantidadEvaluados", cantidadEvaluados);
            node.put("cantidadNsp", cantidadNsp);
            node.put("promedioNotas", 0);
            if (sumatoriaNotas.compareTo(BigDecimal.ZERO) != 0) {
                node.put("promedioNotas", sumatoriaNotas.divide(new BigDecimal(cantidadEvaluados), 2, RoundingMode.CEILING));
            }
        }

        return node;
    }

    @Override
    public List<Evaluacion> allEvaluacionBySecciones(List<Seccion> secciones) {
        return evaluacionDAO.allBySecciones(secciones);
    }

    @Override
    public List<Evaluacion> allEvaluacionByEvaluacionSeccion(Seccion seccion) {
        return evaluacionDAO.allBySeccion(seccion);
    }

    @Override
    public Map<String, String> allAlumnoEvaluacionBySeccion(Long idSeccion) {
        List<AlumnoEvaluacion> alumnosEvaluaciones = alumnoEvaluacionDAO.allBySeccion(idSeccion);
        Map<String, String> mapNotas = new HashMap();
        for (AlumnoEvaluacion alumnosEvaluacion : alumnosEvaluaciones) {
            mapNotas.put(alumnosEvaluacion.getAlumno().getId() + "-" + alumnosEvaluacion.getEvaluacion().getId(), alumnosEvaluacion.getNota());
        }
        return mapNotas;
    }

    @Override
    public MatriculaSeccion findMatriculaSeccion(Long id) {
        return matriculaSeccionDAO.find(id);
    }

    @Override
    public List<AlumnoEvaluacion> allEvaluacionsByFilter(Alumno alumno, Curso curso, CicloAcademico cicloAcademico) {
        return alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, cicloAcademico);
    }

    @Override
    public AlumnoEvaluacion findAlumnoEvaluacion(Long id, Long idEvaluacion, Long idAlumno) {
        return alumnoEvaluacionDAO.findByFilter(id, idEvaluacion, idAlumno);
    }

    @Override
    @Transactional
    public void saveReclamoNota(ReclamoNota reclamoNota, DataSessionPivot ds) {
        Evaluacion evaluacion = evaluacionDAO.find(reclamoNota.getEvaluacion().getId());

        if (!AlumnoEvaluacion.NSP.equals(reclamoNota.getNotaInicial())) {
            DateTime fechaRealizada = new DateTime(evaluacion.getFechaRealizada());
            DateTime fechaVencimiento = fechaRealizada.plusDays(ReclamoNota.MAXIMO_DIAS_RECLAMO);
            logger.debug("Fecha limite camio de nota {}", fechaVencimiento.toString("dd/MM/yyyy"));
            if (fechaVencimiento.toLocalDate().isBefore(new DateTime().toLocalDate())) {
                throw new PhobosException("Superó la fecha limite para cambiar la nota.");
            }
        }
        reclamoNota.setEstado(EstadoEnum.CRE.name());
        reclamoNota.setFechaReclamo(new Date());
        reclamoNota.setUserReclamo(ds.getUsuario());
        reclamoNotaDAO.save(reclamoNota);

        AlumnoEvaluacion alumnoEvaluacion = alumnoEvaluacionDAO.findByFilter(null, evaluacion.getId(), reclamoNota.getAlumno().getId());
        alumnoEvaluacion.setNota(reclamoNota.getNotaFinal());
        alumnoEvaluacion.setValorNumerico(new BigDecimal(reclamoNota.getNotaFinal()));
        alumnoEvaluacionDAO.update(alumnoEvaluacion);

        //evaluacion.getEvaluacionSeccion().getPlanCalificacion()
        List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByPlan(evaluacion.getEvaluacionSeccion().getPlanCalificacion());

        this.calcularNotasAlumno(reclamoNota.getAlumno(), evaluacion,
                evaluacion.getSeccionResponsable().getGrupoSeccion(),
                evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso(),
                evaluacion.getSeccionResponsable().getGrupoSeccion().getCicloAcademico(),
                evaluacionesPlan);
    }

    @Override
    public Map<Long, MatriculaCurso> getMapMatriculasCursoByCicloCurso(CicloAcademico ciclo, Curso curso) {
        List<MatriculaCurso> lstMatriculaCurso = matriculaCursoDAO.findByCursoCiclo(curso, ciclo);
        Map<Long, MatriculaCurso> resultMap = new HashMap<>();
        for (MatriculaCurso matriculaCurso : lstMatriculaCurso) {
            resultMap.put(matriculaCurso.getMatriculaResumen().getAlumno().getId(), matriculaCurso);
        }
        return resultMap;
    }

    @Override
    public List<Evaluacion> allEvaluacionesByTipoSeccion(Seccion seccion) {

        List<Evaluacion> evaluacionesBySeccion = evaluacionDAO.allBySeccion(seccion);
        List<Evaluacion> evaluacionesBySeccionFinal = new ArrayList<>();
        for (Evaluacion eva : evaluacionesBySeccion) {
            if (!eva.isDesagregado() && eva.getEvaluacionSuperior() == null) {
                eva.setNombreCorto(eva.getTipoEvaluacion().getCodigo() + eva.getNumero());
                eva.setNombreLargo(eva.getTipoEvaluacion().getNombre() + " " + eva.getNumero());
                evaluacionesBySeccionFinal.add(eva);
            }
            if (eva.isDesagregado()) {
                logger.debug("esta desagregado");
                if (eva.getEvaluaciones() == null || eva.getEvaluaciones().isEmpty()) {
                    continue;
                }
                //    Collections.sort(eva.getEvaluaciones(), (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));
                Collections.sort(eva.getEvaluaciones(), (Evaluacion p1, Evaluacion p2) -> p1.getNumero().compareTo(p2.getNumero()));

                logger.debug("hijos {}", eva.getEvaluaciones().size());
                for (Evaluacion evaChild : eva.getEvaluaciones()) {

                    StringBuilder codigoPadre = new StringBuilder();
                    StringBuilder codigoHijo = new StringBuilder();
                    StringBuilder nombreHijo = new StringBuilder();
                    StringBuilder nombrePadre = new StringBuilder();

                    codigoPadre.append(eva.getTipoEvaluacion().getCodigo()).append(eva.getNumero());
                    nombrePadre.append(eva.getTipoEvaluacion().getNombre()).append(" ").append(eva.getNumero());

                    codigoHijo.append(evaChild.getTipoEvaluacion().getCodigo()).append(evaChild.getNumero());
                    nombreHijo.append(evaChild.getTipoEvaluacion().getNombre()).append(" ").append(evaChild.getNumero());

                    evaChild.setNombreCorto("(" + codigoPadre + ")" + codigoHijo);
                    evaChild.setNombreLargo(String.format("%s expandido de %s", nombreHijo, nombrePadre));
                    /*
                    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(evaChild.getTipoEvaluacion().getId());
                    tipoEvaluacion.setNombre(evaChild.getTipoEvaluacion().getNombre());
                    tipoEvaluacion.setCodigo(codigo.toString());
                    evaChild.setTipoEvaluacion(tipoEvaluacion);
                     */
                    evaluacionesBySeccionFinal.add(evaChild);

                }
            }
        }
        return evaluacionesBySeccionFinal;
    }

    @Override
    public void saveEvaluacion(Evaluacion evaluacion) {
        evaluacionDAO.save(evaluacion);
    }

    @Override
    @Transactional
    public void cambiarTipoSeccionEvaluacion(EvaluacionExpandida evaluacionExpandida, TipoSeccionEvalEnum tipoSeccionEvalEnum) {
        logger.debug("Evaluacion Exp {}, Tipo Seccion {}", evaluacionExpandida.getId(), tipoSeccionEvalEnum.name());
        List<Evaluacion> evaluaciones = evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
        for (Evaluacion eva : evaluaciones) {
            if (eva.getFechaRealizada() != null) {
                throw new PhobosException("No se puede cambiar el tipo de sección, ya que cuenta con evaluaciones realizadas.");
            }
        }

        evaluacionDAO.deleteByEvaluacionExpandida(evaluacionExpandida.getId());

        evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionExpandida.getId());
        evaluacionExpandida.setTipoSeccionEnum(tipoSeccionEvalEnum);
        evaluacionExpandidaDAO.update(evaluacionExpandida);
        logger.debug("Evaluacion expandida {}", evaluacionExpandida.getId());

        GrupoSeccion grupoSeccion = evaluacionExpandida.getEvaluacionSeccion().getGrupoSeccion();

        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("Cantidad de secciones para el grupo {}", secciones.size());

        for (Seccion seccionEach : secciones) {
            logger.debug("Seccion Tipo {}", seccionEach.getTipoSeccionEnum().name());
            logger.debug("Tipo evaluacion en seccion {}", seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().name());
            logger.debug("Tipo Evaluacion {}", evaluacionExpandida.getTipoSeccionEnum().name());

            if (seccionEach.getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
                    evaluacionExpandida.getTipoSeccionEnum())) {

                Evaluacion evaluacion = new Evaluacion();
                evaluacion.create(evaluacionExpandida.getEvaluacionSeccion(), seccionEach, evaluacionExpandida);
                this.saveEvaluacion(evaluacion);
            }

        }

    }

    @Transactional
    @Override
    public void deletePlanCalificacion(Long idPlanCalifica, DataSessionPivot ds) {
        PlanCalificacion plan = planCalificacionDAO.find(idPlanCalifica);

        List<EvaluacionSeccion> evalSeccs = evaluacionSeccionDAO.allByPlan(plan);
        for (EvaluacionSeccion evalSecc : evalSeccs) {
            List<Evaluacion> evaluaciones = evaluacionDAO.allByEvaluacionSeccion(evalSecc);
            for (Evaluacion eval : evaluaciones) {
                evaluacionDAO.delete(eval);
            }

            List<EvaluacionExpandida> evalExpans = evaluacionExpandidaDAO.allByEvaluacionSeccion(evalSecc);
            for (EvaluacionExpandida evalExpan : evalExpans) {
                evaluacionExpandidaDAO.delete(evalExpan);
            }
            evaluacionSeccionDAO.delete(evalSecc);
        }

        List<GrupoSeccion> gpoSeccs = grupoSeccionDAO.allByPlan(plan);
        for (GrupoSeccion gpoSecc : gpoSeccs) {
            gpoSecc.setPlanCalificacion(null);
            gpoSecc.setEstadoPlan(null);
            grupoSeccionDAO.update(gpoSecc);
        }

        List<Curso> cursos = cursoDAO.allByPlan(plan);
        for (Curso curso : cursos) {
            curso.setPlanCalificacion(null);
            cursoDAO.update(curso);
        }

        List<EvaluacionPlan> evalPlans = evaluacionPlanDAO.allByPlan(plan);
        for (EvaluacionPlan evalPlan : evalPlans) {
            evaluacionPlanDAO.delete(evalPlan);
        }
        planCalificacionDAO.delete(plan);

    }

    @Transactional
    @Override
    public void saveAceptarExpandir(EvaluacionExpandida[] evaluacionesExpandidas) {
        EvaluacionSeccion evaluacionSeccion = evaluacionesExpandidas[0].getEvaluacionSeccion();
        evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccion.getId());
        List<EvaluacionPlan> evaluacionesPlan = evaluacionPlanDAO.allByFilter(evaluacionSeccion.getPlanCalificacion().getId());
        List<EvaluacionExpandida> evaluacionExpandidasDB = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);

        for (EvaluacionExpandida evaluacionExpandida : evaluacionesExpandidas) {
            List<Evaluacion> evaluacionesPorExp = evaluacionDAO.allByFilter(null, null, null, evaluacionExpandida.getId());
            for (Evaluacion evaluacion : evaluacionesPorExp) {
                if (evaluacion.getFechaIngresoNota() != null) {
                    throw new PhobosException(String.format("Error, la evaluación %s %s ya cuenta con notas ingresadas.",
                            evaluacion.getTipoEvaluacion().getCodigo(),
                            evaluacion.getNumero()));
                }
            }
        }

        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            if (evaluacionPlan.getEvaluacionesExpandidas() == null) {
                evaluacionPlan.setEvaluacionesExpandidas(new ArrayList<>());
            }
            for (EvaluacionExpandida evaluacionExpandida : evaluacionExpandidasDB) {
                if (evaluacionPlan.getTipoEvaluacion().getId().equals(
                        evaluacionExpandida.getTipoEvaluacion().getId())) {
                    evaluacionPlan.getEvaluacionesExpandidas().add(evaluacionExpandida);
                }
            }
        }

        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            for (EvaluacionExpandida evaluacionExpPlan : evaluacionPlan.getEvaluacionesExpandidas()) {
                for (EvaluacionExpandida evalExp : evaluacionesExpandidas) {
                    if (evaluacionExpPlan.getId().equals(evalExp.getId())) {
                        evaluacionExpPlan.setPeso(evalExp.getPeso());
                        evaluacionPlan.setValidarPesoTotal(true);
                        break;
                    }
                }
            }
        }
        List<String> errores = new ArrayList<>();
        for (EvaluacionPlan evaluacionPlan : evaluacionesPlan) {
            if (evaluacionPlan.isValidarPesoTotal()) {

                BigDecimal pesoTotal = evaluacionPlan.getPesoTotal();
                BigDecimal pesoEvals = BigDecimal.ZERO;
                for (EvaluacionExpandida evaluacionExpandida : evaluacionPlan.getEvaluacionesExpandidas()) {
                    pesoEvals = pesoEvals.add(evaluacionExpandida.getPeso());
                }
                if (pesoTotal.compareTo(pesoEvals) != 0) {
                    errores.add(evaluacionPlan.getTipoEvaluacion().getNombre());
                }
            }
        }
        if (!errores.isEmpty()) {
            throw new PhobosException("Error en el porcentaje total de las siguientes evaluaciones : " + StringUtils.join(errores, ", "));
        }

        logger.debug("Evaluacion Seccion {}", evaluacionSeccion.getId());
        for (EvaluacionExpandida evaluacionesExpandida : evaluacionesExpandidas) {
            logger.debug("Id {}, Peso {}", evaluacionesExpandida.getId(), evaluacionesExpandida.getPeso());
            EvaluacionExpandida evaluacionesExpandidaDB = evaluacionExpandidaDAO.find(evaluacionesExpandida.getId());
            evaluacionesExpandidaDB.setPeso(evaluacionesExpandida.getPeso());
            evaluacionExpandidaDAO.update(evaluacionesExpandidaDB);
        }

    }

    @Override
    public List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion) {
        return cursoDAO.allActiveByPlan(planCalificacion);
    }

}
