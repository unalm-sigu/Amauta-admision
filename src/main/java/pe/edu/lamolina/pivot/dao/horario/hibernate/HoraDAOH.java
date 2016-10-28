package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.model.horario.Hora;
import org.springframework.stereotype.Repository;

@Repository
public class HoraDAOH extends AbstractDAO<Hora> implements HoraDAO {

    public HoraDAOH() {
        super();
        setClazz(Hora.class);
    }
}

