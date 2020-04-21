package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.general.SedeDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Sede;

@Repository
public class SedeDAOH extends AbstractEasyDAO<Sede> implements SedeDAO {

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

        return all(sql);
    }
}
