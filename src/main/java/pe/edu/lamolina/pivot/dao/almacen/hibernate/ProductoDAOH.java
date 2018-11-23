package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.pivot.dao.almacen.ProductoDAO;

@Repository
public class ProductoDAOH extends AbstractEasyDAO<Producto> implements ProductoDAO {

    public ProductoDAOH() {
        super();
        setClazz(Producto.class);
    }

}
