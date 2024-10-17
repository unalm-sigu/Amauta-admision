package pe.edu.lamolina.amauta.dao.admision.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaExamenDAO;
import pe.edu.lamolina.model.calificacion.TemaExamen;

@Repository
public class TemaExamenDAOH extends AbstractEasyDAO<TemaExamen> implements TemaExamenDAO {

    public TemaExamenDAOH() {
        super();
        setClazz(TemaExamen.class);
    }

}
