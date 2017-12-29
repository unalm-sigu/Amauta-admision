package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.PlanCalificacion;

public interface EvaluacionPlanDAO extends EasyDAO<EvaluacionPlan> {

    List<EvaluacionPlan> allByDynatable(DynatableFilter filter, Long idPlanCalificacion);

    List<EvaluacionPlan> allByFilter(Long idPlanCalificacion);

    List<EvaluacionPlan> allByPlan(PlanCalificacion planCalificacion);

    void deleleByPlan(PlanCalificacion plan);

    List<EvaluacionPlan> allByPlanes(List<PlanCalificacion> planes);
}
