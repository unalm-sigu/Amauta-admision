package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;

public interface SeccionCursoCachimbosDAO extends EasyDAO<SeccionCursoCachimbos> {

    public List<SeccionCursoCachimbos> allByCurso(Curso curso);

}
