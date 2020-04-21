package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface CursoAdicionalCurriculaDAO extends EasyDAO<CursoAdicionalCurricula> {

    List<CursoAdicionalCurricula> allByDynatable(DynatableFilter filter);

    List<CursoAdicionalCurricula> allByPlanCurricular(PlanCurricular curricula);

    Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas);

}
