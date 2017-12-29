package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface CursoOpcionalCurriculaDAO extends EasyDAO<CursoOpcionalCurricula> {

    List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter);

    Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> planesCurricular);

    List<CursoOpcionalCurricula> allByPlanCurricular(PlanCurricular planCurricular);

    List<CursoOpcionalCurricula> allByNombrePlan(CursoCurricula cursoCurricula, Integer limit);

}
