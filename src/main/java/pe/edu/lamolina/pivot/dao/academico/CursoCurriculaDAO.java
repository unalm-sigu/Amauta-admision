package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

public interface CursoCurriculaDAO extends EasyDAO<CursoCurricula> {

    List<CursoCurricula> allByFilter(TipoCursoCurricula tipoCursoCurricula);

    List<CursoCurricula> allByDynatable(DynatableFilter filter);

    List<CursoCurricula> allByNombrePlanNroCiclo(CursoCurricula cursoCurricula, Integer limit);

    List<CursoCurricula> allByPlanCurricular(PlanCurricular curricula);

    List<CursoCurricula> allByPlanCurricularNroCiclo(PlanCurricular plan, Integer nro);

    Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas);

    List<CursoCurricula> allByTipoCursoCurriculaEnum(TipoCursoCurriculaEnum tipoCursoCurriculaEnum);

    List<CursoCurricula> allByCursoTipoCurriculaEnum(Curso cursoBD, TipoCursoCurriculaEnum tipoCursoCurriculaEnum);

    List<CursoCurricula> allByPlanes(List<PlanCurricular> planes);

    List<CursoCurricula> allByCursoPlan(Curso curso, PlanCurricular planCurricular);

    CursoCurricula findByTipoCC(TipoCursoCurricula tipo);

    CursoCurricula findByCursoAndTipo(TipoCursoCurricula tipo, Curso curso);

}
