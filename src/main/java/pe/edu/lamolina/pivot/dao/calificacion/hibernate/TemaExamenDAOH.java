package pe.edu.lamolina.pivot.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.calificacion.TemaExamenDAO;
import pe.edu.lamolina.pivot.model.calificacion.TemaExamen;
import org.springframework.stereotype.Repository;

@Repository
public class TemaExamenDAOH extends AbstractDAO<TemaExamen> implements TemaExamenDAO {

    public TemaExamenDAOH() {
        super();
        setClazz(TemaExamen.class);
    }
}

