package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface CursoEquivalenteDAO extends EasyDAO<CursoEquivalente> {

    Integer findMaxGrupoByCursoCurricula(CursoCurricula curso);

    void deleteByGrupoCursoCurricula(Integer grupo, CursoCurricula curso);

    List<CursoEquivalente> allActivoByCursoCurricula(CursoCurricula cursoCurricula);

    List<CursoEquivalente> allActivoByCursosCurriculas(List<CursoCurricula> cursosCurricula);

    List<CursoEquivalente> allActivoByPlanCurricular(PlanCurricular planCurricular);

    List<CursoEquivalente> allActivoByPlanes(List<PlanCurricular> planes);

}
