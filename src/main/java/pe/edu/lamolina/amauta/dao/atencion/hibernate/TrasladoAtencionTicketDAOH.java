package pe.edu.lamolina.amauta.dao.atencion.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.atencion.TrasladoAtencionTicket;
import pe.edu.lamolina.amauta.dao.atencion.TrasladoAtencionTicketDAO;

@Repository
public class TrasladoAtencionTicketDAOH extends AbstractEasyDAO<TrasladoAtencionTicket> implements TrasladoAtencionTicketDAO {

    public TrasladoAtencionTicketDAOH() {
        super();
        setClazz(TrasladoAtencionTicket.class);
    }

}
