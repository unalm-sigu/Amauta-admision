package pe.edu.lamolina.amauta.dao.almacen.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.amauta.dao.almacen.ProductoDAO;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

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

    @Override
    public Producto findByCodigo(String codigo) {

        Octavia sql = Octavia.query()
                .from(Producto.class, "prod")
                .join("tipoProducto tip", "unidadPrincipal uni")
                .leftJoin("productoSuperior sup")
                .filter("prod.codigo", codigo)
                .limit(1);
        return find(sql);
    }

//    @Override
//    public List<Producto> allByDynatable(DynatableFilter filter) {
//        DynatableSql sql = new DynatableSql(filter)
//                .select("prod")
//                .from(Inventario.class, "inv")
//                .join("producto prod")
//                .join("oficinaGestora ofi")
//                .filter("ofi.codigo", OficinaEnum.OERA.name())
//                .orderBy("prod.id desc");
//        return all(sql);
//    }

//    @Override
//    public List<Inventario> allByDynatable(DynatableFilter filter) {
//        DynatableSql sql = new DynatableSql(filter)
//                .from(Inventario.class, "in")
//                .join("producto prod")
//                .join("in.oficinaGestora of")
//                .filter("of.codigo", OficinaEnum.OERA.name())
//                .orderBy("prod.id desc");
//        return all(sql);
//    }


    @Override
    public Producto findLastByCodeInventario(CodigoTipoProductoEnum codigoTipoProductoEnum) {
        Octavia sql = Octavia.query()
                .from(Producto.class, "prod")
                .join("tipoProducto tip", "unidadPrincipal uni")
                .leftJoin("productoSuperior sup")
                .filter("tip.codigo", CodigoTipoProductoEnum.BIENES.name())
                .filter("prod.codigo","LIKE","INV%")
                .orderBy("prod.codigo desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<Producto> allProductosOficina() {
        Octavia sql = Octavia.query()
                .from(Producto.class, "prod")
                .join("tipoProducto tip", "unidadPrincipal uni")
                .leftJoin("productoSuperior sup")
                .filter("tip.codigo", CodigoTipoProductoEnum.BIENES.name());
        return all(sql);
    }

    @Override
    public List<Producto> allProductosDera(){
        StringBuilder sql = new StringBuilder();
        sql.append("select inv.* ");
        sql.append("from alm_inventario as inv");
        sql.append("join gen_oficina as off on inv.id_oficina_gestora=off.id");
        sql.append("where off.id=50");

//        Query query = getCurrentSession().createQuery(sql.toString())
//                .addEntity("prod",Producto.class)
//                .addEntity();

        return null;
    }


}
