package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.TipoOficina;

public interface TipoOficinaDAO extends EasyDAO<TipoOficina> {

    TipoOficina findByCodigo(String codigo);

    List<TipoOficina> allByName(String nombre);
}
