package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Repository
public class RolDAOH extends AbstractEasyDAO<Rol> implements RolDAO {

    public RolDAOH() {
        super();
        setClazz(Rol.class);
    }

    @Override
    public List<Rol> allByUser(Usuario usuario, Sistema sistema) {
        Octavia subquery = Octavia.query()
                .from(MenuRol.class, "mr")
                .join("menu me", "me.sistema sm", "rol ro")
                .filter("sm.id", sistema);

        Octavia sql = Octavia.query()
                .selectDistinct("rol")
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol rol")
                .filter("u.id", usuario)
                .filter("estado", EstadoEnum.ACT)
                .exists(subquery)
                .linkedBy("rol.id", "ro.id");

        return all(sql);
    }

    @Override
    public List<Rol> allRolMenu(Menu menu) {
        Octavia sql = Octavia.query()
                .selectDistinct("rol")
                .from(MenuRol.class, "mr")
                .join("menu me", "rol rol")
                .filter("me.id", menu);

        return all(sql);
    }

    @Override
    public List<Rol> allRol(List<Rol> rolesMenu) {
        Octavia sql = Octavia.query()
                .from(Rol.class, "rol")
                .notIn("rol.id", rolesMenu);

        return all(sql);
    }

    @Override
    public List<Rol> allActivoByUsuario(Usuario usuario) {
        Octavia sql = Octavia.query()
                .selectDistinct("rol")
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol rol")
                .filter("u.id", usuario)
                .filter("u.estado", EstadoEnum.ACT);

        return all(sql);

    }

}
