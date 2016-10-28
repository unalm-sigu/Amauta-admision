package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import org.springframework.stereotype.Repository;

@Repository
public class RolDAOH extends AbstractDAO<Rol> implements RolDAO {

    public RolDAOH() {
        super();
        setClazz(Rol.class);
    }
}

