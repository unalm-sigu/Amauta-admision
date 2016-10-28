package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.model.tramite.Tramite;
import org.springframework.stereotype.Repository;

@Repository
public class TramiteDAOH extends AbstractDAO<Tramite> implements TramiteDAO {

    public TramiteDAOH() {
        super();
        setClazz(Tramite.class);
    }
}

