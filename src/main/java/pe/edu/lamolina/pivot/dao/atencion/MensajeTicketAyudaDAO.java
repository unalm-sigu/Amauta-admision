package pe.edu.lamolina.pivot.dao.atencion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.pivot.controller.atencion.TicketAtencionResumen;

public interface MensajeTicketAyudaDAO extends EasyDAO<MensajeTicketAyuda> {

    List<MensajeTicketAyuda> allByDynatable(DynatableFilter filter);

    MensajeTicketAyuda findByTicket(TicketAyuda ticketDb);

    List<MensajeTicketAyuda> allByTicketAyuda(TicketAyuda ticket);

    List<MensajeTicketAyuda> allByTicketExcept(TicketAyuda ticket, MensajeTicketAyuda mensaje);

    TicketAtencionResumen findResumen();

}
