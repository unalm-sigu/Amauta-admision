package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
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
    public List<Rol> all() {
        Octavia sql = Octavia.query()
                .from(Rol.class, "rol")
                .orderBy("rol.nombre");

        return all(sql);
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
                .notIn("rol.id", rolesMenu)
                .orderBy("rol.nombre");

        return all(sql);
    }

    @Override
    public List<Rol> allActivoByUsuario(Usuario usuario) {
        Octavia sql = Octavia.query()
                .selectDistinct("rol")
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol rol")
                .leftJoin("rol.rolSuperior rs")
                .filter("u.id", usuario)
                .filter("ur.estado", EstadoEnum.ACT)
                .filter("u.estado", EstadoEnum.ACT);

        return all(sql);

    }

    @Override
    public Rol findByCode(RolEnum rolEnum) {
        Octavia sql = Octavia.query()
                .from(Rol.class, "r")
                .filter("r.codigo", rolEnum);
        return find(sql);
    }

    @Override
    public List<Rol> allByDynatable(DynatableFilter filter, Sistema sistema) {
//        Octavia subOctavia = new Octavia()
//                .from(RolSistema.class, "rs")
//                .join("sistema sis", "rol r")
//                .filter("sis.id", sistema);

        DynatableSql sql = new DynatableSql(filter)
                .from(Rol.class, "rol")
                .leftJoin("rolSuperior sup")
                .searchFields("codigo", "nombre", "sup.nombre")
                //                .exists(subOctavia)
                //                .linkedBy("rol.id", "r.id")
                .orderBy("rol.id DESC");

        return all(sql);

    }

    @Override
    public List<Rol> allRolSuperior(String nombre) {
        Octavia sql = Octavia.query()
                .from(Rol.class, "r")
                .isNull("r.rolSuperior");
        return all(sql);
    }

    @Override
    public Rol findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Rol.class, "r")
                .filter("r.codigo", codigo);
        return find(sql);
    }

}
