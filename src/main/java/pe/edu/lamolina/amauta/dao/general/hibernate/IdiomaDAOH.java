package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Idioma;

@Repository
public class IdiomaDAOH extends AbstractEasyDAO<Idioma> implements IdiomaDAO {

    public IdiomaDAOH() {
        super();
        setClazz(Idioma.class);
    }

    @Override
    public List<Idioma> allInglesAndEspañol() {
        String[] ids = {"en", "es"};
        Octavia sql = Octavia.query()
                .from(Idioma.class, "idi")
                .in("idi.codigo", ids);
        return all(sql);
    }

    @Override
    public List<Idioma> allByCodigo(List<String> codigos) {
        Octavia sql = Octavia.query()
                .from(Idioma.class, "idi")
                .in("idi.codigo", codigos).
                orderBy("idi.codigo desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Idioma> allDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Idioma.class,"ii")
                .searchFields("nombre", "codigo")
                .orderBy("ii.id desc");

        return all(sql);
    }

}
