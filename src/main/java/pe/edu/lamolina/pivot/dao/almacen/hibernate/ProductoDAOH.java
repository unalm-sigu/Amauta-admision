package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.pivot.dao.almacen.ProductoDAO;

@Repository
public class ProductoDAOH extends AbstractEasyDAO<Producto> implements ProductoDAO {

    public ProductoDAOH() {
        super();
        setClazz(Producto.class);
    }

    @Override
    public List<Producto> allTipoBienes() {

        Octavia sql = Octavia.query()
                .from(Producto.class, "prod")
                .join("tipoProducto tip", "unidadPrincipal uni")
                .leftJoin("productoSuperior sup")
                .filter("tip.codigo", CodigoTipoProductoEnum.BIENES.name())
                .orderBy("prod.nombre", "prod.codigo");
        return all(sql);
    }

}
