package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

public interface CursoTemaExamenDAO extends EasyDAO<CursoTemaExamen> {

    List<CursoTemaExamen> allParents();

    List<CursoTemaExamen> allByCurso(Curso curso);

    List<CursoTemaExamen> allByCursos(List<Curso> cursosNivelacion);

}
