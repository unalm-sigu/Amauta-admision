package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;

public interface CursoOpcionalCurriculaDAO extends Crud<CursoOpcionalCurricula> {

    List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter);

    Map countByPlanesCurricular(List<PlanCurricular> planesCurricular);

}
