package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.TipoProducto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.pivot.dao.almacen.TipoProductoDAO;

@Repository
public class TipoProductoDAOH extends AbstractEasyDAO<TipoProducto> implements TipoProductoDAO {

    public TipoProductoDAOH() {
        super();
        setClazz(TipoProducto.class);
    }

    @Override
    public TipoProducto findByCode(CodigoTipoProductoEnum codigoTipoProductoEnum) {

        Octavia sql = Octavia.query()
                .from(TipoProducto.class, "tp")
                .filter("tp.codigo", codigoTipoProductoEnum.name());
        return find(sql);
    }

}
