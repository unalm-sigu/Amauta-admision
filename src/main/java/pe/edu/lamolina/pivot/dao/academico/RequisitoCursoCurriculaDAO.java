package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;

public interface RequisitoCursoCurriculaDAO extends EasyDAO<RequisitoCursoCurricula> {

    List<RequisitoCursoCurricula> allByCursoCurricula(CursoCurricula cursoCurricula);

    List<RequisitoCursoCurricula> allByRequisitoCurriculaDe(CursoCurricula cursoCurricula);

    List<RequisitoCursoCurricula> allByCursosCurricula(List<CursoCurricula> cursosCurricula);

    List<RequisitoCursoCurricula> allByPlanCurricular(PlanCurricular plan);

    List<RequisitoCursoCurricula> allPostRequisitosByCursosCurricula(List<CursoCurricula> cursosCurricula);

    List<RequisitoCursoCurricula> allByPlanes(List<PlanCurricular> planes);

}
