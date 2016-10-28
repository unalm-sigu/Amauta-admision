package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.AulaExamenDAO;
import pe.edu.lamolina.pivot.model.inscripcion.AulaExamen;
import org.springframework.stereotype.Repository;

@Repository
public class AulaExamenDAOH extends AbstractDAO<AulaExamen> implements AulaExamenDAO {

    public AulaExamenDAOH() {
        super();
        setClazz(AulaExamen.class);
    }
}

