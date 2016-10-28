package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.ColegioDAO;
import pe.edu.lamolina.pivot.model.general.Colegio;
import org.springframework.stereotype.Repository;

@Repository
public class ColegioDAOH extends AbstractDAO<Colegio> implements ColegioDAO {

    public ColegioDAOH() {
        super();
        setClazz(Colegio.class);
    }
}

