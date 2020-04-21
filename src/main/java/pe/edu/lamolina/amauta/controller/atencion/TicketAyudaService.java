package pe.edu.lamolina.amauta.controller.atencion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAtencionResumen;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.model.atencion.TrasladoAtencionTicket;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface TicketAyudaService {

    List<MensajeTicketAyuda> allByDynatable(DynatableFilter filter, Oficina oficina);

    TicketAyuda find(TicketAyuda ticket);

    List<Oficina> allOficinaTicketAyuda();

    MensajeTicketAyuda saverespuesta(MensajeTicketAyuda mensaje, DataSessionPivot ds);

    TicketAtencionResumen findResumen(Oficina oficina);

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

    void asignarme(TicketAyuda ticket, DataSessionPivot ds);

    void asignarColaborador(TicketAyuda ticket, DataSessionPivot ds);

    MensajeTicketAyuda savenota(MensajeTicketAyuda mensaje, DataSessionPivot ds);

    List<Oficina> findoficina(String nombre, Compania compania);

    void trasladocolaborador(TrasladoAtencionTicket traslado, DataSessionPivot ds);

    void trasladooficina(TrasladoAtencionTicket traslado, DataSessionPivot ds);

}
