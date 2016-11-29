package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
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
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

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
    public void createEvaluacionSeccionPorDocente(Docente docente) {

        List<DocenteSeccion> lstDocenteSeccion = docenteSeccionDAO.allByDocente(docente);
        logger.debug("Lista de secciones por docente {}", lstDocenteSeccion.size());
        for (DocenteSeccion docenteSeccion : lstDocenteSeccion) {

            GrupoSeccion grupoSeccion = docenteSeccion.getSeccion().getGrupoSeccion();

            if (grupoSeccion.getEstadoPlan() != null) {
                continue;
            }

            Curso curso = docenteSeccion.getSeccion().getGrupoSeccion().getCurso();

            if (curso.getPlanCalificacion() == null) {
                continue;
            }

            grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PRO);
            grupoSeccionDAO.update(grupoSeccion);

            Long idGrupoSeccion = grupoSeccion.getId();
            Long idPlanCalificacion = curso.getPlanCalificacion().getId();

            EvaluacionSeccion evaluacionSeccion = evaluacionSeccionDAO.findByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
            logger.debug("Encontro la evaluacion seccion : {}", evaluacionSeccion);
            if (evaluacionSeccion == null) {
                EvaluacionSeccion evaluacionSeccionCreate = new EvaluacionSeccion();
                evaluacionSeccionCreate.setPlanCalificacion(new PlanCalificacion(idPlanCalificacion));
                evaluacionSeccionCreate.setGrupoSeccion(new GrupoSeccion(idGrupoSeccion));
                evaluacionSeccionCreate.setEstadoEnum(EstadoPlanCalificaEnum.PRO);
                evaluacionSeccionDAO.save(evaluacionSeccionCreate);
            }
        }
    }

    @Override
    public void createEvaluacionPorEvalSeccion(EvaluacionSeccion evaluacionSeccion) {
        List<Evaluacion> evaluaciones = evaluacionDAO.allByFilter(evaluacionSeccion.getId());
        if (evaluaciones.isEmpty()) {
            List<EvaluacionPlan> evaluacionesPlanes = this.allEvaluacionPlanByPlanCalifica(evaluacionSeccion.getPlanCalificacion().getId());
            for (EvaluacionPlan evaluacionPlan : evaluacionesPlanes) {
                Evaluacion evaluacion = new Evaluacion();
                evaluacion.setAlumnoEvaluacion(null);
                evaluacion.setEvaluacionSeccion(evaluacionSeccion);
                evaluacion.setTipoEvaluacion(evaluacionPlan.getTipoEvaluacion());
                evaluacion.setEstaDesagregado(BigDecimal.ZERO.intValue());
                evaluacion.setEvaluacionSuperior(null);
                evaluacion.setEvaluaciones(null);
                evaluacion.setEvaluados(BigDecimal.ZERO.intValue());
                evaluacion.setPeso(evaluacionPlan.getPesoTotal());
                evaluacionDAO.save(evaluacion);
            }
        }
    }

    @Override
    @Transactional
    public void saveExpansionEvaluacion(Evaluacion evaluacion, DataSession ds) {
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
            evaluacionDAO.save(evaluacionHija);
        }
    }

    @Override
    @Transactional
    public void saveSistemaCalifica(PlanCalificacion planCalificacion, Long grupoSeccionId) {

        planCalificacion.setEstadoEnum(EstadoPlanCalificaEnum.SOL);
        planCalificacion.setFechaRegistro(new Date());
        planCalificacion.setDepartamentoAcademico(new DepartamentoAcademico(1));

        Integer totalWeight = BigDecimal.ZERO.intValue();
        for (EvaluacionPlan evaluacionPlan : planCalificacion.getEvaluacionPlan()) {
            evaluacionPlan.setPlanCalificacion(planCalificacion);
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
        planCalificacionDAO.save(planCalificacion);
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
        return evaluacionDAO.allByFilter(evaluacionSeccion.getId());
    }

    @Override
    public List<SistemaNotas> allSistemasNotas() {
        return sistemaNotasDAO.all();
    }

}
