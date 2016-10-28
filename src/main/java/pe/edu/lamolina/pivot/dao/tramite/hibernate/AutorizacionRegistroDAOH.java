package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.tramite.AutorizacionRegistroDAO;
import pe.edu.lamolina.pivot.model.tramite.AutorizacionRegistro;
import org.springframework.stereotype.Repository;

@Repository
public class AutorizacionRegistroDAOH extends AbstractDAO<AutorizacionRegistro> implements AutorizacionRegistroDAO {

    public AutorizacionRegistroDAOH() {
        super();
        setClazz(AutorizacionRegistro.class);
    }
}

