package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.GrupoRolExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRolExamenDAO;

@Repository
public class GrupoRolExamenDAOH extends AbstractEasyDAO<GrupoRolExamen> implements GrupoRolExamenDAO {

    public GrupoRolExamenDAOH() {
        super();
        setClazz(GrupoRolExamen.class);
    }
}
