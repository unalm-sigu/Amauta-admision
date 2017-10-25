package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.albatross.octavia.Octavia;

@Repository
public class SistemaDAOH extends AbstractDAO<Sistema> implements SistemaDAO {
    
    public SistemaDAOH() {
        super();
        setClazz(Sistema.class);
    }
    
    @Override
    public Sistema findByRolSistema(Rol rol, Sistema sys) {
        Octavia sql = Octavia.query()
                .selectDistinct("sys")
                .from(MenuRol.class, "mr")
                .join("rol rol", "menu me", "me.sistema sys")
                .filter("rol.id", rol)
                .filter("sys.id", sys);
        
        return (Sistema) sql.find(getCurrentSession());
    }
    
}
