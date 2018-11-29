package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.almacen.TipoProducto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.model.enums.EstadoInventarioEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.UnidadMedida;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.almacen.AlmacenDAO;
import pe.edu.lamolina.pivot.dao.almacen.InventarioDAO;
import pe.edu.lamolina.pivot.dao.almacen.ProductoDAO;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;
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

    @Autowired
    ResumenInventarioDAO resumenInventarioDAO;

    @Autowired
    AlmacenDAO almacenDAO;

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
        Inventario inventarioDb = inventarioDAO.find(inventario.getId());
        inventario.setAlmacen(inventarioDb.getAlmacen());
        inventario.setCodigo(inventario.getCodigo().trim());
        inventario.setFechaRegistro(inventarioDb.getFechaRegistro());
        inventario.setUserRegistro(inventarioDb.getUserRegistro());
        inventario.setEstado(inventarioDb.getEstado());
        inventarioDAO.update(inventario);
    }

    @Override
    @Transactional
    public void save(Inventario inventario, Usuario user) {

        Almacen almacen = almacenDAO.findByAula(inventario.getAlmacen().getAula());
        if (almacen == null) {
            almacen = new Almacen();
            almacen.setAula(inventario.getAlmacen().getAula());
            almacen.setUserRegistro(user);
            almacen.setFechaRegistro(new Date());
            almacenDAO.save(almacen);
        }
        inventario.setAlmacen(almacen);
        inventario.setCodigo(inventario.getCodigo().trim());
        inventario.setFechaRegistro(new Date());
        inventario.setUserRegistro(user);
        inventario.setEstadoEnum(EstadoInventarioEnum.DISP);
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
        producto.setCodigo(producto.getCodigo().trim());
        Producto productoRegistrado = productoDAO.findByCodigo(producto.getCodigo());
        if (productoRegistrado != null) {
            throw new PhobosException("Código de producto ya registrado");
        }
        TipoProducto tipoProducto = tipoProductoDAO.findByCode(CodigoTipoProductoEnum.BIENES);
        producto.setTipoProducto(tipoProducto);
        producto.setUnidadPrincipal(new UnidadMedida(1));
        productoDAO.save(producto);
    }

    @Override
    public List<ResumenInventario> allResumenByDynatable(DynatableFilter filter, Aula aula) {
        return resumenInventarioDAO.allByDynatable(filter, aula);
    }

}
