package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.pivot.dao.academico.BecaEstudioDAO;

@Repository
public class BecaEstudioDAOH extends AbstractEasyDAO<BecaEstudio> implements BecaEstudioDAO {

    public BecaEstudioDAOH() {
        super();
        setClazz(BecaEstudio.class);
    }

    @Override
    public List<BecaEstudio> allDynaTable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(BecaEstudio.class, "bec")
                .searchFields("nombre", "institucion")
                .orderBy("bec.id desc");

        return all(sql);
    }

    @Override
    public List<BecaEstudio> allByNombre(List<String> nombre) {
        Octavia sql = Octavia.query()
                .from(BecaEstudio.class, "be")
                .in("be.nombre", nombre).
                orderBy("be.nombre desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<BecaEstudio> allBecaByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(BecaEstudio.class, "bec")
                .beginBlock()
                .__().filter("bec.nombre", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }
}
