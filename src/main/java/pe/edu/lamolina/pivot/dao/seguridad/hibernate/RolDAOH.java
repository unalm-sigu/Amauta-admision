package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import org.springframework.stereotype.Repository;
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

