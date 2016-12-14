package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

@Repository
public class UsuarioRolDAOH extends AbstractDAO<UsuarioRol> implements UsuarioRolDAO {

    public UsuarioRolDAOH() {
        super();
        setClazz(UsuarioRol.class);
    }

    @Override
    public UsuarioRol findByUsuarioAndRol(Usuario usuario, Rol rol) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ur")
                .filter("rol.id", rol)
                .filter("usuario.id", usuario);

        return this.find(sqlUtil);

    }

    @Override
    public void deleteByUsuarioRol(Usuario usuario, List<Long> roles) {

        StringBuilder strQuery = new StringBuilder();
        strQuery.append(" delete from UsuarioRol ")
                .append("   where  usuario.id = :USUARIO")
                .append("       and  rol.id  in ( :ROLES )");

        Query query = getCurrentSession().createQuery(strQuery.toString());
        query.setLong("USUARIO", usuario.getId());
        query.setParameterList("ROLES", roles);
        query.executeUpdate();

    }

}
