package pe.edu.lamolina.pivot.dao.atencion;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.atencion.TicketAyuda;

public interface TicketAyudaDAO extends EasyDAO<TicketAyuda> {

    public TicketAyuda find(TicketAyuda ticket);

}
