package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.NombreCurso;

public interface NombreCursoDAO extends EasyDAO<NombreCurso> {

    List<NombreCurso> allByCurso(Curso curso);

}
