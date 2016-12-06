package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

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
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;

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
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Override
    public List<TipoEvaluacion> allTipoEvaluacion() {
        return tipoEvaluacionDAO.all();
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente) {
        return docenteSeccionDAO.allByCargaAcademica(filter, docente);
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
    public void saveExpansionEvaluacion(Evaluacion evaluacion, DataSessionPivot ds) {
        logger.debug("La evaluacion es {}", evaluacion.getId());

        Evaluacion evaluacionPadre = evaluacionDAO.find(evaluacion.getId());

        for (Evaluacion evaluacionHija : evaluacion.getEvaluaciones()) {
            evaluacionHija.setAlumnoEvaluacion(null);
            evaluacionHija.setEstaDesagregado(BigDecimal.ZERO.intValue());
            evaluacionHija.setEvaluacionSeccion(evaluacionPadre.getEvaluacionSeccion());
            evaluacionHija.setEvaluacionSuperior(evaluacionPadre);
            evaluacionHija.setEvaluaciones(null);
            evaluacionHija.setEvaluados(BigDecimal.ZERO.intValue());
            evaluacionHija.setExtemporaneos(BigDecimal.ZERO.intValue());
            evaluacionHija.setFechaDesagregar(new Date());
            evaluacionHija.setPeso(evaluacionHija.getPeso());
            evaluacionHija.setTipoEvaluacion(evaluacionHija.getTipoEvaluacion());
            evaluacionHija.setTipoSeccion(evaluacionPadre.getTipoSeccion());
            evaluacionHija.setUsuarioDesagregar(ds.getUsuario());

            evaluacionHija.getEvaluacionSeccion().getGrupoSeccion();
            //traer claves,
            //las evaluaciones por tantas claves exista
            //en el recorrido identificar que evaluaciones le pertenecen a la clave en actualmente recorrida

            evaluacionDAO.save(evaluacionHija);
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
    public void aceptarExpansion(Long evaluacionSeccionId, DataSessionPivot ds) {
        logger.debug("La evaluacionSeccionId es {}", evaluacionSeccionId);

        EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.find(evaluacionSeccionId);
        evaluacionSeccion.setEstadoEnum(EstadoPlanCalificaEnum.EXP);
        evaluacionSeccionDAO.update(evaluacionSeccion);

        GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.EXP);
        grupoSeccionDAO.update(grupoSeccion);

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
    public List<MatriculaSeccion> allMatriculaSeccionByFilters(Seccion seccion) {
        return matriculaSeccionDAO.allByFilters(seccion);
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
        SistemaNotas sistemaNotas = evaluacion.getEvaluacionSeccion().getSistemaNotas();

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
            if (sistemaNotas.isNumerico()) {
                alumnoEvaluacion.setValorNumerico(new BigDecimal(alumnoEvaluacion.getNota()));
            }
            alumnoEvaluacion.setUsuarioIngresoNota(ds.getUsuario());
            alumnoEvaluacion.setEstado("");
            alumnoEvaluacionDAO.save(alumnoEvaluacion);
        }
    }

    @Override
    public SistemaNotas findSistemaNotaById(Long id) {
        return sistemaNotasDAO.find(id);
    }

}
