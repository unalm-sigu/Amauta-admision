package pe.edu.lamolina.amauta.dao.almacen;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.almacen.TipoProducto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;

public interface TipoProductoDAO extends EasyDAO<TipoProducto> {

    public TipoProducto findByCode(CodigoTipoProductoEnum codigoTipoProductoEnum);

}
