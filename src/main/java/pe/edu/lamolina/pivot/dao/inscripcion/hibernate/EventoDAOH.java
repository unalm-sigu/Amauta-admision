package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.edu.lamolina.pivot.dao.inscripcion.EventoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.Evento;

@Repository
public class EventoDAOH extends AbstractEasyDAO<Evento> implements EventoDAO {

    public EventoDAOH() {
        super();
        setClazz(Evento.class);
    }

    @Override
    public Evento findByCode(String exam) {
        Octavia sqlUtil = Octavia.query()
                .from(Evento.class)
                .filter("codigo", exam);
        return (Evento) sqlUtil.find(getCurrentSession());
    }
}

