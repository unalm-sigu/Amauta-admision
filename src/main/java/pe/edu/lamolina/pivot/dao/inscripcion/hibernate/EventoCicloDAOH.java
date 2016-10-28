package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.EventoCicloDAO;
import pe.edu.lamolina.pivot.model.inscripcion.EventoCiclo;
import org.springframework.stereotype.Repository;

@Repository
public class EventoCicloDAOH extends AbstractDAO<EventoCiclo> implements EventoCicloDAO {

    public EventoCicloDAOH() {
        super();
        setClazz(EventoCiclo.class);
    }
}

