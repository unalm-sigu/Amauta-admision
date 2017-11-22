package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;

public interface CursoCurriculaDAO extends Crud<CursoCurricula> {

    CursoCurricula find(Long id);

    List<CursoCurricula> allByFilter(TipoCursoCurricula tipoCursoCurricula);

    List<CursoCurricula> allByDynatable(DynatableFilter filter);

    List<CursoCurricula> allByNombreFilter(Long planCurriculaId, Integer numeroCiclo, String nombre, Integer limit);

    void updateCreditoRequisito(CursoCurricula cursoCurricula);

    List<CursoCurricula> allByPlan(PlanCurricular planCurricular);

}
