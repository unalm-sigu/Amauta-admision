package pe.edu.lamolina.pivot.controller.atencion;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.edu.lamolina.pivot.dao.atencion.MensajeTicketAyudaDAO;
import pe.edu.lamolina.pivot.dao.atencion.TicketAyudaDAO;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAtencionResumen;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.model.atencion.TrasladoAtencionTicket;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoTicketAyudaEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.enums.PrioridadTicketAyudaEnum;
import pe.edu.lamolina.model.enums.TipoMensajeTicketAyudaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoTrasladoTicketEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.pivot.dao.atencion.TrasladoAtencionTicketDAO;
import pe.edu.lamolina.pivot.dao.general.ArchivoDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TicketAyudaServiceImp implements TicketAyudaService {

    @Autowired
    MailerService mailerService;

    @Autowired
    TicketAyudaDAO ticketAyudaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    MensajeTicketAyudaDAO mensajeTicketAyudaDAO;

    @Autowired
    ArchivoDAO archivoDAO;

    @Autowired
    StorageService swiftService;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    TrasladoAtencionTicketDAO trasladoAtencionTicketDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public List<MensajeTicketAyuda> allByDynatable(DynatableFilter filter, Oficina oficina) {
                return mensajeTicketAyudaDAO.allByDynatable(filter,oficina);
    }

    @Override
    public TicketAyuda find(TicketAyuda ticket) {

        if (ticket.getId() == null) {
            return new TicketAyuda();
        }

        TicketAyuda ticketDb = ticketAyudaDAO.find(ticket.getId());

        MensajeTicketAyuda mensaje = mensajeTicketAyudaDAO.findByTicket(ticketDb);
        List<Archivo> archivos = archivoDAO.allByInstanciaTipoInstancia(mensaje.getId(), InstanciaEnum.TICKET_AYUDA);
        mensaje.setArchivos(archivos);
        ticketDb.setMensajeTicketAyuda(mensaje);
        List<MensajeTicketAyuda> mensajess = mensajeTicketAyudaDAO.allByTicketExcept(ticketDb, mensaje);
        ticketDb.setMensajesTicketAyuda(mensajess);
        return ticketDb;

    }

    @Override
    public List<Oficina> allOficinaTicketAyuda() {
        return oficinaDAO.allByNivel(TipoOficinaEnum.OFI);
    }

    @Override
    @Transactional
    public MensajeTicketAyuda saverespuesta(MensajeTicketAyuda mensaje, DataSessionPivot ds) {

        TicketAyuda ticket = ticketAyudaDAO.find(mensaje.getTicketAyuda());

        if (ticket.getColaborador() == null) {
            Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(ticket.getOficina(), ds.getPersona());
            ticket.setColaborador(colaborador);
        }
        ticket.setEstadoEnum(EstadoTicketAyudaEnum.RESPONDIDO);
        ticketAyudaDAO.update(ticket);

        MensajeTicketAyuda mensajePrincipal = mensajeTicketAyudaDAO.findByTicket(ticket);
        mensajePrincipal.setEstadoEnum(EstadoTicketAyudaEnum.RESPONDIDO);
        mensajeTicketAyudaDAO.update(mensajePrincipal);

        mensaje.setFechaRegistro(new Date());
        mensaje.setUserRegistro(ds.getUsuario());
        mensaje.setTipoEnum(TipoMensajeTicketAyudaEnum.RESPUESTA);
        mensaje.setPrioridad(PrioridadTicketAyudaEnum.NORMAL.name());
        mensaje.setEstado(EstadoTicketAyudaEnum.ACTIVO.name());
        mensajeTicketAyudaDAO.save(mensaje);

        this.sendNotificacionTicketRespuesta(ticket);
        return mensaje;
    }

    private void sendNotificacionTicketRespuesta(TicketAyuda ticket) {

        ContenidoCarta contenido = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HELPDESK_RESPUESTA);
        mailerService.enviarNotificacionTicketHelpDesk(ticket.getPersona(), contenido);

    }

    private void sendNotificacionTicketCreate(TicketAyuda ticket) {

        ContenidoCarta contenido = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HELPDESK_NUEVO);
        mailerService.enviarNotificacionTicketHelpDesk(ticket.getPersona(), contenido);

    }

    @Override
    public TicketAtencionResumen findResumen(Oficina oficina) {
        return mensajeTicketAyudaDAO.findResumen(oficina);
    }

    @Override
    public List<Colaborador> allColaboradorByOficina(Oficina oficina) {
        return colaboradorDAO.allActivosByOficina(oficina);
    }

    @Override
    @Transactional
    public void asignarme(TicketAyuda ticket, DataSessionPivot ds) {

        TicketAyuda ticketDb = ticketAyudaDAO.find(ticket.getId());
        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(ticketDb.getOficina(), ds.getPersona());
        ticketDb.setColaborador(colaborador);

        ticketAyudaDAO.update(ticketDb);

    }

    @Override
    @Transactional
    public void asignarColaborador(TicketAyuda ticket, DataSessionPivot ds) {

        TicketAyuda ticketDb = ticketAyudaDAO.find(ticket.getId());
        ticketDb.setColaborador(ticket.getColaborador());
        ticketAyudaDAO.update(ticketDb);

    }

    @Override
    @Transactional
    public MensajeTicketAyuda savenota(MensajeTicketAyuda mensaje, DataSessionPivot ds) {

        TicketAyuda ticket = ticketAyudaDAO.find(mensaje.getTicketAyuda());

        mensaje.setFechaRegistro(new Date());
        mensaje.setUserRegistro(ds.getUsuario());
        mensaje.setTipoEnum(TipoMensajeTicketAyudaEnum.NOTA);
        mensaje.setPrioridad(PrioridadTicketAyudaEnum.NORMAL.name());
        mensaje.setEstado(EstadoTicketAyudaEnum.ACTIVO.name());
        mensajeTicketAyudaDAO.save(mensaje);

        //this.sendNotificacionTicketCreate(ticket);
        return mensaje;
    }

    @Override
    public List<Oficina> findoficina(String nombre, Compania compania) {
        return oficinaDAO.allByNombre(nombre, compania);
    }

    @Override
    @Transactional
    public void trasladocolaborador(TrasladoAtencionTicket traslado, DataSessionPivot ds) {

        TicketAyuda ticket = ticketAyudaDAO.find(traslado.getTicketAyuda());
        traslado.setColaboradorOrigen(ticket.getColaborador());
        traslado.setTipoEnum(TipoTrasladoTicketEnum.TCOL);
        traslado.setFechaTraslado(new Date());
        trasladoAtencionTicketDAO.save(traslado);

        ticket.setEstadoEnum(EstadoTicketAyudaEnum.TCOL);
        ticket.setColaborador(traslado.getColaboradorDestino());
        ticket.setFechaColaborador(new Date());
        ticketAyudaDAO.update(ticket);

    }

    @Override
    @Transactional
    public void trasladooficina(TrasladoAtencionTicket traslado, DataSessionPivot ds) {

        TicketAyuda ticket = ticketAyudaDAO.find(traslado.getTicketAyuda());

        traslado.setOficinaOrigen(ticket.getOficina());
        traslado.setTipoEnum(TipoTrasladoTicketEnum.TOFI);
        traslado.setFechaTraslado(new Date());
        trasladoAtencionTicketDAO.save(traslado);

        ticket.setEstadoEnum(EstadoTicketAyudaEnum.TOFI);
        ticket.setOficina(traslado.getOficinaDestino());
        ticket.setColaborador(null);
        ticket.setFechaColaborador(null);
        ticketAyudaDAO.update(ticket);

    }

}
