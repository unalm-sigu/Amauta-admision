package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.TipoProducto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.model.enums.TipoArticuloEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.UnidadMedida;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.almacen.InventarioDAO;
import pe.edu.lamolina.pivot.dao.almacen.ProductoDAO;
import pe.edu.lamolina.pivot.dao.almacen.TipoProductoDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;

@Service
@Transactional(readOnly = true)
public class InventarioAulaServiceImp implements InventarioAulaService {

    @Autowired
    InventarioDAO inventarioDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    ProductoDAO productoDAO;

    @Autowired
    TipoProductoDAO tipoProductoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Aula findAula(Long idaula) {
        return aulaDAO.find(idaula);
    }

    @Override
    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula) {
        return inventarioDAO.allByDynatable(filter, aula);
    }

    @Override
    @Transactional
    public void update(Inventario inventario) {
        inventarioDAO.update(inventario);
    }

    @Override
    @Transactional
    public void save(Inventario inventario, Usuario user) {
        inventarioDAO.save(inventario);
    }

    @Override
    @Transactional
    public void delete(Inventario inventario) {
        inventarioDAO.delete(inventario);
    }

    @Override
    public Inventario find(Inventario inventario) {
        return inventarioDAO.find(inventario.getId());
    }

    @Override
    public List<Producto> allProducto() {
        List<Producto> productos = productoDAO.allTipoBienes();
        List<Producto> masters = productos.stream().filter(producto -> producto.getProductoSuperior() == null).collect(Collectors.toList());
        Map<Long, List<Producto>> productosMasterMap = TypesUtil.convertListToMapList("productoSuperior.id", productos);
        masters.stream().forEach(producto -> {
            List<Producto> childproduct = productosMasterMap.get(producto.getId());
            producto.setProductos(childproduct);
        });
        return masters;
    }

    @Override
    @Transactional
    public void saveProducto(Producto producto, Usuario user) {
        TipoProducto  tipoProducto = tipoProductoDAO.findByCode(CodigoTipoProductoEnum.BIENES);
        producto.setTipoProducto(tipoProducto);
        producto.setUnidadPrincipal(new UnidadMedida(1));
        productoDAO.save(producto);
    }

}
