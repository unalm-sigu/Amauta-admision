package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import static pe.edu.lamolina.pivot.zelper.enums.UserEstadoEnum.ACT;

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

    @Override
    public List<UsuarioRol> allByUser(Usuario user) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ur")
                .filter("usuario.id", user);

        return this.all(sqlUtil);
    }

    @Override
    public List<UsuarioRol> allByUsuarios(List<Usuario> users) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .in("u.id", users)
                .filter("ur.estado", ACT);

        return sql.all(getCurrentSession());
    }
    
    
    @Override
    public UsuarioRol findByUsuarioRol(Usuario usuario, Rol rol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .filter("u.id", usuario)
                .isNull("ur.fechaFin")
                .filter("r.id", rol);

        return (UsuarioRol) sql.find(getCurrentSession());
    }
    
    @Override
    public UsuarioRol find(UsuarioRol userRol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .filter("ur.id", userRol);

        return (UsuarioRol) sql.find(getCurrentSession());
    }

}
