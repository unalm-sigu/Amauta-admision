package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.almacen.InventarioDAO;

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
                .join("almacen al", "al.aula au", "producto pro")
                .searchFields("inv.comentario", "inv.codigo", "pro.nombre", "pro.codigo")
                .filter("au.id", aula)
                .orderBy("inv.id desc");
        return all(sql);
    }

    @Override
    public List<Inventario> allById(List<Inventario> inventarios) {
        Octavia sql = Octavia.query()
                .from(Inventario.class, "inv")
                .join("almacen al", "al.aula au", "producto pro")
                .in("inv.id", inventarios);
        return all(sql);
    }

}
