package pe.edu.lamolina.pivot.dao.horario.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.horario.TipoHoraDAO;
import pe.edu.lamolina.pivot.model.horario.TipoHora;
import org.springframework.stereotype.Repository;

@Repository
public class TipoHoraDAOH extends AbstractDAO<TipoHora> implements TipoHoraDAO {

    public TipoHoraDAOH() {
        super();
        setClazz(TipoHora.class);
    }
}

