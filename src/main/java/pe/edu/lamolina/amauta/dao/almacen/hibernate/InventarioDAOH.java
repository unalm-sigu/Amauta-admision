package pe.edu.lamolina.amauta.dao.almacen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.amauta.dao.almacen.InventarioDAO;

import static pe.edu.lamolina.model.constantines.AcademicoConstantine.ID_OFICINA_OERA;

@Repository
public class InventarioDAOH extends AbstractEasyDAO<Inventario> implements InventarioDAO {

    public InventarioDAOH() {
        super();
        setClazz(Inventario.class);
    }

    @Override
    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Inventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro","oficinaGestora off")
                .searchFields("inv.comentario", "inv.codigo", "pro.nombre", "pro.codigo")
                .filter("au.id", aula)
                .filter("off.id", ID_OFICINA_OERA)
                .orderBy("inv.id desc");
        return all(sql);
    }

    @Override
    public List<Inventario> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Inventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro","oficinaGestora off")
                .searchFields("inv.comentario","au.nombre", "inv.codigo", "pro.nombre", "pro.codigo")
                .filter("off.id", ID_OFICINA_OERA)
                .orderBy("au.nombre asc");
        return all(sql);
    }

    @Override
    public List<Inventario> allById(List<Inventario> inventarios) {
        Octavia sql = Octavia.query()
                .from(Inventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro", "oficinaGestora off")
                .in("inv.id", inventarios)
                .filter("off.id", ID_OFICINA_OERA);
        return all(sql);
    }

    @Override
    public List<Inventario> findByAlmacen(Almacen almacen){
        Octavia sql = Octavia.query()
                .from(Inventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro", "oficinaGestora off")
                .filter("al.id", almacen)
                .filter("off.id", ID_OFICINA_OERA);
        return all(sql);
    }

    @Override
    public Inventario findLastCodeInventarioByOficina() {
        Octavia sql = Octavia.query()
                .from(Inventario.class,"inv")
                .join("oficinaGestora off")
                .filter("off.id",ID_OFICINA_OERA)
                .orderBy("inv.id desc")
                .limit(1);
        return find(sql);
    }

}
