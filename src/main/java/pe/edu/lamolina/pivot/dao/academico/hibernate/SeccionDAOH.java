package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import org.springframework.stereotype.Repository;

@Repository
public class SeccionDAOH extends AbstractDAO<Seccion> implements SeccionDAO {

    public SeccionDAOH() {
        super();
        setClazz(Seccion.class);
    }
}

