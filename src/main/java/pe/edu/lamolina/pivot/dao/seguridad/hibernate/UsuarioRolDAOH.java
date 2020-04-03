package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
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
    public List<UsuarioRol> allByUsuarioMenu(Usuario usuario, Menu menu) {
        Octavia subquery = Octavia.query()
                .from(MenuRol.class, "mr")
                .join("menu m", "rol r2")
                .filter("m.id", menu);

        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r1")
                .filter("u.id", usuario)
                .exists(subquery)
                .linkedBy("r1.id", "r2.id");

        return all(sql);
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
    public UsuarioRol findByUsuarioAndRolAndEstadoUsuRol(Usuario usuario, Rol rol, UserEstadoEnum estadoUsuarioRol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("rol rol", "usuario u")
                .filter("rol.id", rol)
                .filter("ur.estado", estadoUsuarioRol)
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
                .leftJoin("rol.rolSuperior rsup")
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
    public List<UsuarioRol> allByUsuarioRol(Usuario usuario, Rol rol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r")
                .filter("u.id", usuario)
                .filter("r.id", rol)
                .isNull("ur.fechaFin");

        return all(sql);
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
    public UsuarioRol findByUserOficina(Usuario usuario, Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "oficina ofi")
                .filter("u.id", usuario.getId())
                .filter("ofi.id", oficina.getId())
                .filter("ur.estado", ACT);

        return find(sql);
    }

    @Override
    public List<UsuarioRol> allByUserOficina(Usuario usuario1, Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "oficina ofi")
                .filter("u.id", usuario1.getId())
                .filter("ofi.id", oficina.getId())
                .filter("ur.estado", ACT);

        return all(sql);
    }

    @Override
    public void updateInactivar(Colaborador colaborador, Usuario usuario) {
        StringBuilder strb = new StringBuilder();
        strb.append("update UsuarioRol  set estado=:estado where oficina.id=:oficinaId and usuario.id = :usuario ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("oficinaId", colaborador.getOficina().getId());
        query.setParameter("usuario", usuario.getId());
        query.setParameter("estado", INA.toString());
        query.executeUpdate();

    }

    @Override
    public List<UsuarioRol> findByUsuario(Usuario usuario) {
        Octavia sql = new Octavia()
                .from(UsuarioRol.class, "ur")
                .join("oficina ofi", "ofi.tipoOficina tof", "usuario us")
                .filter("us.id", usuario)
                .in("tof.codigo", Arrays.asList(TipoOficinaEnum.ESP, TipoOficinaEnum.FAC))
                .filter("ofi.estado", ACT)
                .filter("ur.estado", ACT);

        return all(sql);
    }

    @Override
    public List<UsuarioRol> allActivosByUser(Usuario usuario) {
        Octavia sql = new Octavia()
                .from(UsuarioRol.class, "ur")
                .join("usuario us", "rol rol")
                .leftJoin("oficina")
                .filter("us.id", usuario)
                .filter("ur.estado", ACT);

        return all(sql);
    }

    @Override
    public List<UsuarioRol> allWithOfficeByUserRol(Usuario usuario, Rol rol) {
        Octavia sql = new Octavia()
                .from(UsuarioRol.class, "ur")
                .join("oficina ofi", "ofi.tipoOficina tof", "usuario us", "rol rol")
                .filter("us.id", usuario)
                .filter("rol.id", rol)
                .filter("ofi.estado", ACT)
                .filter("ur.estado", ACT);

        return all(sql);
    }

    @Override
    public UsuarioRol findByOficinaRolUser(OficinaEnum oficinaEnum, Rol role, Usuario usuario) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r", "oficina ofi")
                .filter("ur.estado", UserEstadoEnum.ACT)
                .filter("u.id", usuario)
                .filter("ofi.codigo", oficinaEnum)
                .filter("r.id", role);

        return find(sql);
    }

}
