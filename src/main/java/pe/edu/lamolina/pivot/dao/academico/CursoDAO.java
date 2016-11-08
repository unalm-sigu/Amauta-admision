package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Curso;

public interface CursoDAO extends Crud<Curso> {

    List<Curso> allAutocomplete(String nombre);

}
