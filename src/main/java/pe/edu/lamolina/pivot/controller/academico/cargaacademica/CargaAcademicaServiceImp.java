package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
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
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

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
    public Curso findCurso(Long idCurso) {
        return cursoDAO.find(idCurso);
    }
    
    @Override
    public Seccion findSeccion(Long idSeccion) {
        return seccionDAO.find(idSeccion);
    }
    
    @Override
    public GrupoSeccion findGrupo(Long idGrupoSeccion) {
        return grupoSeccionDAO.find(idGrupoSeccion);
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
            
            if (curso.getPlanCalificacion() == null || curso.getPlanCalificacion().getId() == null) {
                logger.debug("el curso no cuenta con plan calificacion");
                continue;
            }
            
            Long idGrupoSeccion = grupoSeccion.getId();
            Long idPlanCalificacion = curso.getPlanCalificacion().getId();
            logger.debug("Grupo seccion {}, plan calificacion {}", idGrupoSeccion, idPlanCalificacion);
            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, idGrupoSeccion);
            if (evaluacionSeccion != null) {
                logger.debug("el grupo ya cuenta con evaluacion seccion");
            } else {
                PlanCalificacion planCalificacion = planCalificacionDAO.find(idPlanCalificacion);
                
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
                for (int i = 1; i <= evaluacionPlan.getCantidadEvaluaciones().intValue(); i++) {
                    EvaluacionExpandida evaluacion = new EvaluacionExpandida();
                    evaluacion.setAlumnoEvaluacion(null);
                    evaluacion.create(evaluacionSeccion, evaluacionPlan, i);
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
    public void saveExpansionEvaluacion(EvaluacionExpandida evaluacion, DataSessionPivot ds) {
        logger.debug("La evaluacion es {}", evaluacion.getId());
        
        EvaluacionExpandida evaluacionPadre = evaluacionExpandidaDAO.find(evaluacion.getId());
        evaluacionPadre.setEstaDesagregado(BigDecimal.ONE.intValue());
        evaluacionPadre.setFechaDesagregar(new Date());
        evaluacionPadre.setUsuarioDesagregar(ds.getUsuario());
        
        Integer newPesoTotal = 0;
        for (EvaluacionExpandida evaluacionHija : evaluacion.getEvaluaciones()) {
            newPesoTotal = newPesoTotal + evaluacionHija.getPeso();
        }
        logger.debug("new peso total {}, eva padre peso {}", newPesoTotal, evaluacionPadre.getPeso());
        if (newPesoTotal != evaluacionPadre.getPeso()) {
            throw new PhobosException("El peso de las evaluaciones debe ser igual a " + evaluacionPadre.getPeso());
        }
        int numero = 1;
        for (EvaluacionExpandida evaluacionHija : evaluacion.getEvaluaciones()) {
            evaluacionHija.setAlumnoEvaluacion(null);
            evaluacionHija.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evaluacionHija.setEvaluacionSeccion(evaluacionPadre.getEvaluacionSeccion());
            evaluacionHija.setEvaluacionSuperior(evaluacionPadre);
            evaluacionHija.setEvaluaciones(null);
            evaluacionHija.setEvaluados(BigDecimal.ZERO.intValue());
            evaluacionHija.setExtemporaneos(BigDecimal.ZERO.intValue());
            evaluacionHija.setFechaDesagregar(null);
            evaluacionHija.setPeso(evaluacionHija.getPeso());
            evaluacionHija.setTipoEvaluacion(evaluacionHija.getTipoEvaluacion());
            evaluacionHija.setTipoSeccion(evaluacionPadre.getTipoSeccion());
            evaluacionHija.setUsuarioDesagregar(null);
            evaluacionHija.setNumero(numero);
            evaluacionHija.getEvaluacionSeccion().getGrupoSeccion();
            numero++;
            evaluacionExpandidaDAO.save(evaluacionHija);
        }
        evaluacionExpandidaDAO.update(evaluacionPadre);
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
        
        Integer totalWeight = BigDecimal.ZERO.intValue();
        Boolean errorPesoEvaluacion = Boolean.FALSE;
        
        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
            evaluacionPlan.setPlanCalificacion(planCalificacion);
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }
            if (evaluacionPlan.getEvaluacionesObligatorias() == null) {
                evaluacionPlan.setEvaluacionesObligatorias(BigDecimal.ZERO.intValue());
            }
            totalWeight += evaluacionPlan.getPesoTotal();
        }
        if (totalWeight != 100) {
            throw new PhobosException("Pesos total de las evaluaciones incorrecto.");
        }
        if (errorPesoEvaluacion) {
            throw new PhobosException("Peso evaluacion incorrecto..");
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
        return evaluacionDAO.allByFilter(evaluacionSeccion.getId(), null, null);
    }
    
    @Override
    public List<EvaluacionExpandida> allEvaluacionesExpByEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        return evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
    }
    
    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }
    
    @Override
    @Transactional
    public void aceptarRechazo(Long cursoId, Long seccionId, DataSessionPivot ds) {
        logger.debug("CursoId {}, SeccionId {}", cursoId, seccionId);
        
        Curso curso = cursoDAO.find(cursoId);
        Seccion seccion = seccionDAO.find(seccionId);
        
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, seccion.getGrupoSeccion().getId());
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
    public void aceptarPlanCalificacion(Long cursoId, Long seccionId, DataSessionPivot ds) {
        logger.debug("CursoId {}, SeccionId {}", cursoId, seccionId);
        
        Curso curso = cursoDAO.find(cursoId);
        Seccion seccion = seccionDAO.find(seccionId);
        
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(null, seccion.getGrupoSeccion().getId());
        logger.debug("La evaluacion seccion es {}", evaluacionSeccion.getId());
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.ACEP);
        evaluacionSeccionDAO.update(evaluacionSeccion);
        
        this.createEvaluacionExpPorEvalSeccion(evaluacionSeccion, EstadoPlanCalificaEnum.ACEP);
        
        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        /*   grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.ACEP);
        grupoSeccionDAO.update(grupoSeccion);*/
        
        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("la cantidad de secciones para el grupo {}, es {}", grupoSeccion.getId(), secciones.size());
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", seccion.getGrupoSeccion().getPlanCalificacion().getId(), planEvaluaciones.size());
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
        
    }
    
    @Override
    @Transactional
    public void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds) {
        logger.debug("La evaluacionSeccionId es {}", evaluacionSeccionId);
        
        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccionId);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.EXP);
        evaluacionSeccionDAO.update(evaluacionSeccion);
        
        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.EXP);
        grupoSeccionDAO.update(grupoSeccion);

        /////
        List<Seccion> secciones = seccionDAO.allByFilter(grupoSeccion.getId());
        logger.debug("la cantidad de secciones para el grupo {}, es {}", grupoSeccion.getId(), secciones.size());
        List<EvaluacionExpandida> planEvaluaciones = evaluacionExpandidaDAO.allByFilter(evaluacionSeccion.getId(), null);
        logger.debug("Plan Calificacion {}, Cantidad de Evaluaciones {}", grupoSeccion.getPlanCalificacion().getId(), planEvaluaciones.size());
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
        
    }
    
    @Override
    public DocenteSeccion findDocenteSeccion(Long idDocenteSeccion) {
        return docenteSeccionDAO.find(idDocenteSeccion);
    }
    
    @Override
    public List<Evaluacion> allEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        return evaluacionDAO.allByFilter(null, idGrupoSeccion, null);
    }
    
    @Override
    public List<AlumnoEvaluacion> allAlumnoEvaluacionByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        return alumnoEvaluacionDAO.allByFilter(null, idGrupoSeccion, null);
    }
    
    @Override
    public List<Evaluacion> findBySeccion(Long idSeccion) {
        return evaluacionDAO.allByFilter(null, null, idSeccion);
    }
    
    @Override
    public EvaluacionExpandida findEvaluacionExpandida(Long idEvaluacionPlan) {
        return evaluacionExpandidaDAO.find(idEvaluacionPlan);
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
    public void saveIngresoNotas(DataSessionPivot ds, Evaluacion evaluacionParam, AlumnoEvaluacion[] alumnosEvaluaciones) {
        Date today = new Date();
        
        Evaluacion evaluacion = evaluacionDAO.find(evaluacionParam.getId());
        PlanCalificacion planCalificacion = evaluacion.getEvaluacionSeccion().getPlanCalificacion();
        CicloAcademico ciclo = evaluacion.getSeccionResponsable().getGrupoSeccion().getCicloAcademico();
        SistemaNotas sistemaNotas = sistemaNotasDAO.find(evaluacion.getEvaluacionSeccion().getSistemaNotas().getId());
        
        Seccion seccion = seccionDAO.find(evaluacion.getSeccionResponsable().getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        
        evaluacion.setFechaIngresoNota(today);
        evaluacion.setEvaluados(alumnosEvaluaciones.length);
        evaluacionDAO.update(evaluacion);
        
        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            AlumnoEvaluacion alumnoEvaluacion = new AlumnoEvaluacion();
            alumnoEvaluacion.setAlumno(alumnoEvaluacionEach.getAlumno());
            alumnoEvaluacion.setEvaluacion(alumnoEvaluacionEach.getEvaluacion());
            alumnoEvaluacion.setFechaIngresoNota(today);
            alumnoEvaluacion.setNota(alumnoEvaluacionEach.getNota());
            alumnoEvaluacion.setEsIngresoRegular(BigDecimal.ONE.intValue());
            
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
        
        BigDecimal bd100 = new BigDecimal("100");
        for (AlumnoEvaluacion alumnoEvaluacionEach : alumnosEvaluaciones) {
            Alumno alumno = alumnoEvaluacionEach.getAlumno();
            Curso curso = evaluacion.getSeccionResponsable().getGrupoSeccion().getCurso();
            List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, ciclo);
            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);
            
            BigDecimal pesoTotal = BigDecimal.ZERO;
            BigDecimal ponderado = BigDecimal.ZERO;
            for (AlumnoEvaluacion ae : evaluacionesAlumno) {
                BigDecimal peso = null;
                if (ae.getEvaluacion().getId() == evaluacion.getId().longValue()) {
                    peso = new BigDecimal(evaluacion.getPeso());
                } else {
                    peso = new BigDecimal(ae.getEvaluacion().getPeso());
                }
                pesoTotal = pesoTotal.add(peso);
                ponderado = ponderado.add(peso.multiply(ae.getValorNumerico()));
            }
            ponderado = ponderado.divide(pesoTotal, 2, RoundingMode.HALF_UP);
            BigDecimal avance = ponderado.divide(bd100, 2, RoundingMode.HALF_UP);
            matriculaCurso.setNotaAvance(NumberFormat.notaDecimal(ponderado));
            matriculaCurso.setNotaAcumulada(NumberFormat.notaDecimal(avance));
            matriculaCurso.setPorcentajeAvanceNota(pesoTotal.intValue());
            
            if (pesoTotal.compareTo(bd100) == 0) {
                BigDecimal nf = ponderado.divide(bd100, 0, RoundingMode.HALF_UP);
                matriculaCurso.setNotaFinal(NumberFormat.nota(nf));
            }
            matriculaCursoDAO.update(matriculaCurso);
        }
        
        if (evaluacionDAO.countEvaluacionesFaltantesByGrupo(grupoSeccion.getId()).intValue() == 0) {
            grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.CER);
            grupoSeccionDAO.update(seccion.getGrupoSeccion());
        }
    }
    
    @Override
    public SistemaNotas findSistemaNotaById(Long id) {
        return sistemaNotasDAO.find(id);
    }
    
    @Override
    public ObjectNode getDetalleEvaluacion(Long idEvaluacion, Long idDocenteSeccion) {
        Evaluacion evaluacion = this.findEvaluacion(idEvaluacion);
        logger.debug("evaluacion param {}, {}", idEvaluacion, evaluacion == null ? "no encontro" : "si encontro");
        
        DocenteSeccion docenteSeccion = this.findDocenteSeccion(idDocenteSeccion);
        List<AlumnoEvaluacion> alumnosEvaluaciones = this.allAlumnoEvaluacionByFilter(null, null, docenteSeccion.getSeccion().getId());
        GrupoSeccion grupoSeccion = this.findGrupo(docenteSeccion.getSeccion().getGrupoSeccion().getId());
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
        reclamoNota.setEstado(EstadoEnum.CRE.name());
        reclamoNota.setFechaReclamo(new Date());
        reclamoNota.setUserReclamo(ds.getUsuario());
        reclamoNotaDAO.save(reclamoNota);
    }
    
}
