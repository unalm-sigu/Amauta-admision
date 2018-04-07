package pe.edu.lamolina.pivot.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.TipoOficina;

public interface TipoOficinaDAO extends EasyDAO<TipoOficina> {

    TipoOficina findByCodigo(String codigo);
}
