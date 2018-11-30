package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;

@Repository
public class ResumenInventarioDAOH extends AbstractEasyDAO<ResumenInventario> implements ResumenInventarioDAO {

    public ResumenInventarioDAOH() {
        super();
        setClazz(ResumenInventario.class);
    }

    @Override
    public List<ResumenInventario> allByDynatable(DynatableFilter filter, Aula aula) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ResumenInventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro", "pro.productoSuperior ps")
                .searchFields("pro.nombre", "pro.codigo","ps.nombre", "ps.codigo")
                .filter("au.id", aula)
                .orderBy("inv.id desc");
        return all(sql);
    }

    @Override
    public ResumenInventario findByAlmacenProducto(Almacen almacen, Producto producto) {
        Octavia sql = Octavia.query()
                .from(ResumenInventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro")
                .filter("al.id", almacen)
                .filter("pro.id", producto);
        return find(sql);
    }

    @Override
    public List<ResumenInventario> allVisiblesByAulas(List<Aula> aulas) {
        Octavia sql = Octavia.query()
                .from(ResumenInventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro")
                .filter("inv.visibleReporteParcial", 1)
                .in("au.id", aulas);
        return all(sql);
    }

}
