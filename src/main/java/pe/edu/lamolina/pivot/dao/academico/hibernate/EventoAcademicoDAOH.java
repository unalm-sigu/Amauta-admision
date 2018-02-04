package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.pivot.dao.academico.EventoAcademicoDAO;

@Repository
public class EventoAcademicoDAOH extends AbstractEasyDAO<EventoAcademico> implements EventoAcademicoDAO {

    public EventoAcademicoDAOH() {
        super();
        setClazz(EventoAcademico.class);
    }

}
