package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.GrupoEspecialExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoEspecialExamenDAO;

@Repository
public class GrupoEspecialExamenDAOH extends AbstractEasyDAO<GrupoEspecialExamen> implements GrupoEspecialExamenDAO {

    public GrupoEspecialExamenDAOH() {
        super();
        setClazz(GrupoEspecialExamen.class);
    }
}
