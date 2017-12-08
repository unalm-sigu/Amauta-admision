package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;

public interface ResumenPlanCurricularDAO extends Crud<ResumenPlanCurricular> {

    ResumenPlanCurricular findByTipoCursoCurrPlan(TipoCursoCurricula tipoCursoCurricula, PlanCurricular planCurricular);

    List<ResumenPlanCurricular> allByDynatable(DynatableFilter filter);

}
