package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

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
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SistemaNotasDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
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

    @Override
    public List<TipoEvaluacion> allTipoEvaluacion() {
        return tipoEvaluacionDAO.all();
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
    public List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter) {
        return planCalificacionDAO.allByDynatable(dynatableFilter);
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
        planCalificacionDAO.update(planCalificacion);
    }

    @Override
    @Transactional
    public void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        PlanCalificacion planCalificacion = planCalificacionDAO.find(idPLanCalificacion);
        planCalificacion.setEstadoEnum(estadoPlanCalificaEnum);
        planCalificacionDAO.update(planCalificacion);
    }

    @Override
    public List<Curso> allCursosByPlanCalifica(DynatableFilter dynatableFilter, Long planCalificacion) {
        return cursoDAO.allByDynatable(dynatableFilter, planCalificacion);
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
