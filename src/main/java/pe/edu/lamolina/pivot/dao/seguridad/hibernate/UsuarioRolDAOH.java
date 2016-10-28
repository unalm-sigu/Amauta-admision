package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRolDAOH extends AbstractDAO<UsuarioRol> implements UsuarioRolDAO {

    public UsuarioRolDAOH() {
        super();
        setClazz(UsuarioRol.class);
    }
}

