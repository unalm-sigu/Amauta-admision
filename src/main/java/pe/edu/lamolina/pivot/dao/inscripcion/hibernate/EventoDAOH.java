package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.EventoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.Evento;
import org.springframework.stereotype.Repository;

@Repository
public class EventoDAOH extends AbstractDAO<Evento> implements EventoDAO {

    public EventoDAOH() {
        super();
        setClazz(Evento.class);
    }
}

