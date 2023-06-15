package pe.edu.lamolina.amauta.controller.general.producto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.almacen.ProductoDAO;
import pe.edu.lamolina.model.almacen.Producto;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoServiceImp implements ProductoService {

    @Autowired
    ProductoDAO productoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Producto> allProductosByAulas() {
        List<Producto> productos=productoDAO.allProductosOficina();
        return productos;
    }

//    @Override
//    public List<Producto> allByDynatable(DynatableFilter filter) {
//        List<Producto> productos=productoDAO.allByDynatable(filter);
//        return productos;
//    }
}
