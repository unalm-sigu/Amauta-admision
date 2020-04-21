package pe.edu.lamolina.amauta.dao.horario.hibernate;

import pe.edu.lamolina.amauta.dao.horario.TipoHoraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.horario.TipoHora;

@Repository
public class TipoHoraDAOH extends AbstractEasyDAO<TipoHora> implements TipoHoraDAO {

    public TipoHoraDAOH() {
        super();
        setClazz(TipoHora.class);
    }
}
