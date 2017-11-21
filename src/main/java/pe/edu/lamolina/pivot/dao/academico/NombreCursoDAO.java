package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.NombreCurso;

public interface NombreCursoDAO extends Crud<NombreCurso> {

    List<NombreCurso> allByCurso(Curso curso);

}

