package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.*;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;

@Repository
public class SeccionGrupoRegularDAOH extends AbstractEasyDAO<SeccionGrupoRegular> implements SeccionGrupoRegularDAO {

    public SeccionGrupoRegularDAOH() {
        super();
        setClazz(SeccionGrupoRegular.class);
    }

}
