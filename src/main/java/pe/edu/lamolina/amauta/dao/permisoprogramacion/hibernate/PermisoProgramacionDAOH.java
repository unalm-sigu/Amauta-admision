package pe.edu.lamolina.amauta.dao.permisoprogramacion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.amauta.dao.permisoprogramacion.PermisoProgramacionDAO;

@Repository
public class PermisoProgramacionDAOH extends AbstractEasyDAO<PermisoProgramacion> implements PermisoProgramacionDAO {

    public PermisoProgramacionDAOH() {
        super();
        setClazz(PermisoProgramacion.class);
    }

    @Override
    public List<PermisoProgramacion> allPermisos() {
        Octavia sql = new Octavia()
                .from(PermisoProgramacion.class, "pp")
                .orderBy("nivel");
        
        return all(sql);
    }

}
