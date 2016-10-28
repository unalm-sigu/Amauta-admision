package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PabellonDAO;
import pe.edu.lamolina.pivot.model.general.Pabellon;
import org.springframework.stereotype.Repository;

@Repository
public class PabellonDAOH extends AbstractDAO<Pabellon> implements PabellonDAO {

    public PabellonDAOH() {
        super();
        setClazz(Pabellon.class);
    }
}

