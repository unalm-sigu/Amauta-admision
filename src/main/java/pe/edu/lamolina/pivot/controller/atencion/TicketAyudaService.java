package pe.edu.lamolina.pivot.controller.atencion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TicketAyudaService {

    List<MensajeTicketAyuda> allByDynatable(DynatableFilter filter);

    TicketAyuda find(TicketAyuda ticket);

    List<Oficina> allOficinaTicketAyuda();

    MensajeTicketAyuda saverespuesta(MensajeTicketAyuda mensaje, DataSessionPivot ds);

    TicketAtencionResumen findResumen();

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

    void asignarme(TicketAyuda ticket, DataSessionPivot ds);

    void asignarColaborador(TicketAyuda ticket, DataSessionPivot ds);

    MensajeTicketAyuda savenota(MensajeTicketAyuda mensaje, DataSessionPivot ds);

}
