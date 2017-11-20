package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;

public interface CursoOpcionalCurriculaDAO extends Crud<CursoOpcionalCurricula> {

    List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter);

}
