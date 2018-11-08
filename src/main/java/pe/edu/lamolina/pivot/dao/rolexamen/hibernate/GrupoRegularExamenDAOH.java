package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;

@Repository
public class GrupoRegularExamenDAOH extends AbstractEasyDAO<GrupoRegularExamen> implements GrupoRegularExamenDAO {

    public GrupoRegularExamenDAOH() {
        super();
        setClazz(GrupoRegularExamen.class);
    }

}
