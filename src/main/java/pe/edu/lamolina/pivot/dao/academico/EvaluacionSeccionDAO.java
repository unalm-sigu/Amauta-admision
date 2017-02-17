package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

public interface EvaluacionSeccionDAO extends Crud<EvaluacionSeccion> {

    EvaluacionSeccion findByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum);

    EvaluacionSeccion find(Long id);

    List<EvaluacionSeccion> allByPlan(PlanCalificacion plan);

    List<EvaluacionSeccion> allByGrupoSeccion(GrupoSeccion gpoSecc);

}
