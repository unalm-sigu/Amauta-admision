package pe.edu.lamolina.pivot.dao.migraciones;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;

public interface HistoMyDAO extends EasyDAO<HistoMy> {

    HistoMy findByHisto(HistoGradMy histo);

    List<HistoMy> allByMatricula(String matricula);

}
