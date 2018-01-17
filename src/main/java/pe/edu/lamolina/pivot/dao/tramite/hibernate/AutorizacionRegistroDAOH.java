package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import pe.edu.lamolina.pivot.dao.tramite.AutorizacionRegistroDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;

@Repository
public class AutorizacionRegistroDAOH extends AbstractEasyDAO<AutorizacionRegistro> implements AutorizacionRegistroDAO {

    public AutorizacionRegistroDAOH() {
        super();
        setClazz(AutorizacionRegistro.class);
    }
}
