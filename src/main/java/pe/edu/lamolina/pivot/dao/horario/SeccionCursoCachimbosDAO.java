package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;

public interface SeccionCursoCachimbosDAO extends EasyDAO<SeccionCursoCachimbos> {

    public List<SeccionCursoCachimbos> allByCursoCachimbos(CursoCachimbos curso);

    public void deleteByCursoCachimbos(CursoCachimbos curso);

}
