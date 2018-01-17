package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.edu.lamolina.pivot.dao.general.PabellonDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Pabellon;

@Repository
public class PabellonDAOH extends AbstractEasyDAO<Pabellon> implements PabellonDAO {

    public PabellonDAOH() {
        super();
        setClazz(Pabellon.class);
    }
}
