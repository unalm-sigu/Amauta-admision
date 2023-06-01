package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.ItemInformeFinalTutoria;

public interface ItemInformeFinalTutoriaDAO extends EasyDAO<ItemInformeFinalTutoria> {

    List<ItemInformeFinalTutoria> allByInforme(InformeFinalTutoria informe);

}
