package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.SedeDAO;
import pe.edu.lamolina.pivot.model.general.Sede;
import org.springframework.stereotype.Repository;

@Repository
public class SedeDAOH extends AbstractDAO<Sede> implements SedeDAO {

    public SedeDAOH() {
        super();
        setClazz(Sede.class);
    }
}

