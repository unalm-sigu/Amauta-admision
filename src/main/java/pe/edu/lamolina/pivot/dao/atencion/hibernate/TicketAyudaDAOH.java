package pe.edu.lamolina.pivot.dao.atencion.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.atencion.TicketAyudaDAO;
import pe.edu.lamolina.model.atencion.TicketAyuda;

@Repository
public class TicketAyudaDAOH extends AbstractEasyDAO<TicketAyuda> implements TicketAyudaDAO {

    public TicketAyudaDAOH() {
        super();
        setClazz(TicketAyuda.class);
    }

    @Override
    public TicketAyuda find(TicketAyuda ticket) {
        Octavia sql = Octavia.query()
                .from(TicketAyuda.class, "ta")
                .join("persona per", "oficina ofi")
                .leftJoin("colaborador co")
                .filter("ta.id", ticket);
        return find(sql);
    }

}
