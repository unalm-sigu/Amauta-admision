package pe.edu.lamolina.amauta.dao.almacen;

import java.util.List;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;

public interface ProductoDAO extends EasyDAO<Producto> {

    public List<Producto> allTipoBienes();

    public Producto findByCodigo(String codigo);

//    List<Producto> allByDynatable(DynatableFilter filter);
//    List<Inventario> allByDynatable(DynatableFilter filter);

    public Producto findLastByCodeInventario(CodigoTipoProductoEnum codigoTipoProductoEnum);

}
