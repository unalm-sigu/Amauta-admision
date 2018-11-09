package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.SeccionCursoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoRegularDAO;

@Repository
public class SeccionCursoRegularDAOH extends AbstractEasyDAO<SeccionCursoRegular> implements SeccionCursoRegularDAO {

    public SeccionCursoRegularDAOH() {
        super();
        setClazz(SeccionCursoRegular.class);
    }
}
