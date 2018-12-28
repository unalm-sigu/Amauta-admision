package pe.edu.lamolina.pivot.dao.almacen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;

public interface ProductoDAO extends EasyDAO<Producto> {

    public List<Producto> allTipoBienes();

    public Producto findByCodigo(String codigo);

    public Producto findLastByCodeInventario(CodigoTipoProductoEnum codigoTipoProductoEnum);

}
