package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.RequisitoCursoCurricula;

public interface RequisitoCursoCurriculaDAO extends Crud<RequisitoCursoCurricula> {

    List<RequisitoCursoCurricula> allByCursoCurricula(CursoCurricula cursoCurricula);

}
