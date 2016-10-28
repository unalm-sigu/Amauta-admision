package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.model.tramite.RetiroCiclo;
import org.springframework.stereotype.Repository;

@Repository
public class RetiroCicloDAOH extends AbstractDAO<RetiroCiclo> implements RetiroCicloDAO {

    public RetiroCicloDAOH() {
        super();
        setClazz(RetiroCiclo.class);
    }
}

