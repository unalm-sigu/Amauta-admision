package pe.edu.lamolina.pivot.dao.migraciones;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.croacia.HistoGradMy;

public interface HistoGradMyDAO extends EasyDAO<HistoGradMy> {

    List<HistoGradMy> allByMatricula(String matricula);

}
