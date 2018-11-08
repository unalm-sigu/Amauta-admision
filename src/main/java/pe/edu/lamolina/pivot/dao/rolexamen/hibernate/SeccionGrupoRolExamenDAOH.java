package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRolExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRolExamenDAO;

@Repository
public class SeccionGrupoRolExamenDAOH extends AbstractEasyDAO<SeccionGrupoRolExamen> implements SeccionGrupoRolExamenDAO {

    public SeccionGrupoRolExamenDAOH() {
        super();
        setClazz(SeccionGrupoRolExamen.class);
    }

}
