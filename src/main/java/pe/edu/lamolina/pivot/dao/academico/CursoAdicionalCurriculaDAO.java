package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.octavia.dynatable.DynatableFilter;

import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;

public interface CursoAdicionalCurriculaDAO extends Crud<CursoAdicionalCurricula> {

    List<CursoAdicionalCurricula> allByDynatable(DynatableFilter filter);

    List<CursoAdicionalCurricula> allByPlanCurricular(PlanCurricular curricula);

    Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas);

}
