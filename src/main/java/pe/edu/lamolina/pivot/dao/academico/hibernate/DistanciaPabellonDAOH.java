package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import pe.edu.lamolina.pivot.model.academico.DistanciaPabellon;
import org.springframework.stereotype.Repository;

@Repository
public class DistanciaPabellonDAOH extends AbstractDAO<DistanciaPabellon> implements DistanciaPabellonDAO {

    public DistanciaPabellonDAOH() {
        super();
        setClazz(DistanciaPabellon.class);
    }
}

