package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOH extends AbstractDAO<Usuario> implements UsuarioDAO {

    public UsuarioDAOH() {
        super();
        setClazz(Usuario.class);
    }
}

