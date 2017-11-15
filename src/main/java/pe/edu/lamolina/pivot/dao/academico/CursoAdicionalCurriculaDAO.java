package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.octavia.dynatable.DynatableFilter;

import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;

public interface CursoAdicionalCurriculaDAO extends Crud<CursoAdicionalCurricula> {

    List<CursoAdicionalCurricula> allByDynatable(DynatableFilter filter);

}
