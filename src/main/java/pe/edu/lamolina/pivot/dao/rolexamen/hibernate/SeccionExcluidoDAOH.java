package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;

@Repository
public class SeccionExcluidoDAOH extends AbstractEasyDAO<SeccionExcluido> implements SeccionExcluidoDAO {

    public SeccionExcluidoDAOH() {
        super();
        setClazz(SeccionExcluido.class);
    }

}
