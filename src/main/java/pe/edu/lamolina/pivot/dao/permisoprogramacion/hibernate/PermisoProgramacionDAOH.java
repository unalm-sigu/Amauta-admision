package pe.edu.lamolina.pivot.dao.permisoprogramacion.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.PermisoProgramacionDAO;

@Repository
public class PermisoProgramacionDAOH extends AbstractEasyDAO<PermisoProgramacion> implements PermisoProgramacionDAO {

    public PermisoProgramacionDAOH() {
        super();
        setClazz(PermisoProgramacion.class);
    }

}
