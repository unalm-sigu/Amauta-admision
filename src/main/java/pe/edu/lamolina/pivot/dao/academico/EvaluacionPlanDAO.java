package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

public interface EvaluacionPlanDAO extends Crud<EvaluacionPlan> {

    List<EvaluacionPlan> allByDynatable(DynatableFilter filter, Long idPlanCalificacion);

    List<EvaluacionPlan> allByFilter(Long idPlanCalificacion);

    List<EvaluacionPlan> allByPlan(PlanCalificacion planCalificacion);
}
