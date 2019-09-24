package pe.edu.lamolina.pivot.dao.migraciones;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.croacia.HistoMy;

public interface HistoMyDAO extends EasyDAO<HistoMy> {

    List<HistoMy> allByMatricula(String matricula);

}
