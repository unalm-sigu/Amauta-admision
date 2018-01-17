package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;

public interface SeccionCursoCachimbosDAO extends EasyDAO<SeccionCursoCachimbos> {

    List<SeccionCursoCachimbos> allByCursoCachimbos(CursoCachimbos curso);

    void deleteByCursoCachimbos(CursoCachimbos curso);

    List<SeccionCursoCachimbos> allByCursoCachimbos(List<CursoCachimbos> cursoCachimbos);

    List<SeccionCursoCachimbos> allByCiclo(CicloAcademico ciclo);

}
