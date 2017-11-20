package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;

public interface PlanCurricularDAO extends Crud<PlanCurricular> {

    PlanCurricular find(Long id);

    List<PlanCurricular> allByDynatable(DynatableFilter filter, Facultad facultad);

}
