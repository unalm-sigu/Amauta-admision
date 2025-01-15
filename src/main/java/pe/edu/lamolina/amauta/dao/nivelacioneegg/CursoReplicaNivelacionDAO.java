package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;

public interface CursoReplicaNivelacionDAO extends EasyDAO<CursoReplicaNivelacion> {

    List<CursoReplicaNivelacion> allByParents();

    List<CursoReplicaNivelacion> allByCursoNivelacion(Curso curso);

}
