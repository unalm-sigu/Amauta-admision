package pe.edu.lamolina.pivot.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.calificacion.InfoExamenDAO;
import pe.edu.lamolina.pivot.model.calificacion.InfoExamen;
import org.springframework.stereotype.Repository;

@Repository
public class InfoExamenDAOH extends AbstractDAO<InfoExamen> implements InfoExamenDAO {

    public InfoExamenDAOH() {
        super();
        setClazz(InfoExamen.class);
    }
}

