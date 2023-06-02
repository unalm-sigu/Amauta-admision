package pe.edu.lamolina.amauta.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.general.InventarioTraslado;
import java.util.List;

public interface InventarioTrasladoDAO extends EasyDAO<InventarioTraslado> {

    List<InventarioTraslado> allByInventario(Inventario inventario);

}
