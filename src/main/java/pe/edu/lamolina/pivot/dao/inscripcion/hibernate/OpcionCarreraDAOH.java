package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.OpcionCarreraDAO;
import pe.edu.lamolina.pivot.model.inscripcion.OpcionCarrera;
import org.springframework.stereotype.Repository;

@Repository
public class OpcionCarreraDAOH extends AbstractDAO<OpcionCarrera> implements OpcionCarreraDAO {

    public OpcionCarreraDAOH() {
        super();
        setClazz(OpcionCarrera.class);
    }
}

