package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import org.springframework.stereotype.Repository;

@Repository
public class OrientacionCarreraDAOH extends AbstractDAO<OrientacionCarrera> implements OrientacionCarreraDAO {

    public OrientacionCarreraDAOH() {
        super();
        setClazz(OrientacionCarrera.class);
    }
}

