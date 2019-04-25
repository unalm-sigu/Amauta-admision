package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;

public interface ResumenPlanCurricularDAO extends EasyDAO<ResumenPlanCurricular> {

    ResumenPlanCurricular findByTipoCursoCurrPlan(TipoCursoCurricula tipoCursoCurricula, PlanCurricular planCurricular);

    List<ResumenPlanCurricular> allByDynatable(DynatableFilter filter);

    List<ResumenPlanCurricular> allByPlan(PlanCurricular planBD);

    public List<ResumenPlanCurricular> allByPlanes(List<PlanCurricular> planesAlumnos);

}
