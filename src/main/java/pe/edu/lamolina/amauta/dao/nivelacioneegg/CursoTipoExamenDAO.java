package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoTipoExamen;

public interface CursoTipoExamenDAO extends EasyDAO<CursoTipoExamen> {

    List<CursoTipoExamen> allByCurso(Curso curso);

}
