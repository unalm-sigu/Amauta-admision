package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.SedeDAO;
import pe.edu.lamolina.pivot.model.general.Sede;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;

@Repository
public class SedeDAOH extends AbstractDAO<Sede> implements SedeDAO {

    public SedeDAOH() {
        super();
        setClazz(Sede.class);
    }

    @Override
    public List<Sede> allSedesByName(String nombre) {
         Octavia sql = Octavia.query()
                .from(Sede.class, "se")
                .join("compania cia")
                .filter("se.nombre", "like", nombre);
        return sql.all(getCurrentSession());
    }
}

