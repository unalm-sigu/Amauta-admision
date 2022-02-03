package pe.edu.lamolina.amauta.dao.log.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.log.UsuarioProgramacionDAO;
import pe.edu.lamolina.model.log.UsuarioProgramacionLogger;

@Repository
public class UsuarioProgramacionDAOH extends AbstractEasyDAO<UsuarioProgramacionLogger> implements UsuarioProgramacionDAO {

    public UsuarioProgramacionDAOH() {
        super();
        setClazz(UsuarioProgramacionLogger.class);
    }

}
