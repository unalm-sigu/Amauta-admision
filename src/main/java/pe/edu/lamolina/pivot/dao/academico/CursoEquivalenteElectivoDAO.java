package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface CursoEquivalenteElectivoDAO extends EasyDAO<CursoEquivalenteElectivo> {

    Integer findMaxGrupoByCursoOpcionalCurricula(CursoOpcionalCurricula curso);

    void deleteByGrupoCursoOpcionalCurricula(Integer grupo, CursoOpcionalCurricula curso);

    List<CursoEquivalenteElectivo> allActivoByCursoOpcionalCurricula(CursoOpcionalCurricula cursoCurricula);

    List<CursoEquivalenteElectivo> allActivoByPlanCurricular(PlanCurricular planCurricular);
}
