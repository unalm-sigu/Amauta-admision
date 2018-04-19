package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Repository
public class UsuarioRolDAOH extends AbstractEasyDAO<UsuarioRol> implements UsuarioRolDAO {

    public UsuarioRolDAOH() {
        super();
        setClazz(UsuarioRol.class);
    }

    @Override
    public UsuarioRol findByUsuarioAndRol(Usuario usuario, Rol rol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("rol rol", "usuario u")
                .filter("rol.id", rol)
                .filter("u.id", usuario);

        return find(sql);

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
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("rol rol", "usuario u")
                .filter("u.id", user);

        return all(sql);
    }

    @Override
    public List<UsuarioRol> allByUsuarios(List<Usuario> users) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .in("u.id", users)
                .filter("ur.estado", ACT);

        return all(sql);
    }

    @Override
    public UsuarioRol findByUsuarioRol(Usuario usuario, Rol rol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .filter("u.id", usuario)
                .isNull("ur.fechaFin")
                .filter("r.id", rol);

        return find(sql);
    }

    @Override
    public UsuarioRol find(UsuarioRol userRol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .filter("ur.id", userRol);

        return find(sql);
    }

    @Override
    public UsuarioRol findUsuarioAndOficina(Usuario usuario1, Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "oficina ofi")
                .filter("u.id", usuario1.getId())
                .filter("ofi.id", oficina.getId())
                .filter("ur.estado", ACT);

        return find(sql);
    }

}
