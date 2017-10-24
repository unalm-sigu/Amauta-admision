package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class RolDAOH extends AbstractDAO<Rol> implements RolDAO {

    public RolDAOH() {
        super();
        setClazz(Rol.class);
    }
    
    
    @Override
    public List<Rol> allByUser(Usuario usuario, Sistema sistema) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct rol ");
        sql.append("   from ").append(UsuarioRol.class.getName()).append(" as ur ");
        sql.append("  inner join ur.usuario u ");
        sql.append("  inner join ur.rol rol ");
        sql.append("  where u.id = :USER ");
        sql.append("    and ur.estado = :ESTADO ");
        sql.append("    and exists ( ");
        sql.append("           select mr.id ");
        sql.append("             from ").append(MenuRol.class.getSimpleName()).append(" as mr ");
        sql.append("             join mr.menu me ");
        sql.append("             join me.sistema sm ");
        sql.append("             join mr.rol ro ");
        sql.append("            where ro.id = rol.id ");
        sql.append("              and sm.id = :SISTEMA ");
        sql.append("    ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("USER", usuario.getId());
        query.setLong("SISTEMA", sistema.getId());
        query.setString("ESTADO", EstadoEnum.ACT.name());

        return query.list();
    }

    @Override
    public List<Rol> allRolMenu(Menu menu) {

        StringBuilder sql = new StringBuilder();
        sql.append("  select distinct rol ");
        sql.append("  from ").append(MenuRol.class.getName()).append(" as mero ");
        sql.append("  inner join mero.menu me ");
        sql.append("  inner join mero.rol rol ");
        sql.append("  where me.id = :MENU ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("MENU", menu.getId());

        return query.list();
    }

    @Override
    public List<Rol> allRol(List<Rol> rolesMenu) {
        Octavia sql = Octavia.query()
                .from(Rol.class, "rol")
                .notIn("rol.id", rolesMenu);

        return sql.all(getCurrentSession());
    }

    
    
    @Override
    public List<Rol> allActivoByUsuario(Usuario usuario) {

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct r ");
        sql.append("   from ").append(UsuarioRol.class.getName()).append(" ur ");
        sql.append("  inner join ur.rol r");
        sql.append("  inner join ur.usuario u ");
        sql.append("  where u.id = :USUARIO ");
        sql.append("    and u.estado = :ESTADO ");
        
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("USUARIO", usuario.getId());
        query.setString("ESTADO", EstadoEnum.ACT.name());
        return query.list();

    }

    
    
}

