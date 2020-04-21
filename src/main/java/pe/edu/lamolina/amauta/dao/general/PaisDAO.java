package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Pais;

public interface PaisDAO extends EasyDAO<Pais> {

    List<Pais> allPaisesByName(String forLike);

    Pais findByCodigo(String codigo);

}
