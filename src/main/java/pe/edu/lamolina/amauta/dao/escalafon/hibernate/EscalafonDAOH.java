package pe.edu.lamolina.amauta.dao.escalafon.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.escalafon.EscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.general.Persona;

@Repository
public class EscalafonDAOH extends AbstractEasyDAO<Escalafon> implements EscalafonDAO {

    public EscalafonDAOH() {
        super();
        setClazz(Escalafon.class);
    }

    @Override
    public List<Escalafon> allDynatableFilter(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Escalafon.class, "es")
                .join("persona p")
                .searchComplexField("concat(coalesce(p.paterno,''),' ',coalesce(p.materno,''),' ',coalesce(p.nombres,''))")
                .searchFields("p.nombres", "p.materno", "p.paterno")
                .orderBy("es.id desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public Escalafon find(Escalafon escalafon) {
        Octavia sql = new Octavia()
                .from(Escalafon.class, "es")
                .join("persona p", "paisNacimiento")
                .filter("es.id", escalafon.getId());

        return find(sql);
    }

    @Override
    public Escalafon findByPersona(Persona persona) {
        Octavia sql = new Octavia()
                .from(Escalafon.class, "es")
                .join("persona p")
                .leftJoin("paisNacimiento")
                .filter("p.id", persona);

        return find(sql);
    }
}
