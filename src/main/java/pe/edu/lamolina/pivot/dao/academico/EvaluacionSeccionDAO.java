package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

public interface EvaluacionSeccionDAO extends Crud<EvaluacionSeccion> {

    EvaluacionSeccion findByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion);

    EvaluacionSeccion find(Long id);

    public List<EvaluacionSeccion> allByPlan(PlanCalificacion plan);

}
