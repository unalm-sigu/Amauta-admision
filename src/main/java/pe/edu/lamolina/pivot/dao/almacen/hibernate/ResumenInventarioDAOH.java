package pe.edu.lamolina.pivot.dao.almacen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Inventario;
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
                .join("almacen al", "al.aula au", "producto pro")
                .searchFields("inv.comentario", "inv.codigo", "pro.nombre", "pro.codigo")
                .filter("au.id", aula)
                .orderBy("inv.id desc");
        return all(sql);
    }

}
