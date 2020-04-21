package pe.edu.lamolina.amauta.zelper.mail;

import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface MailerService {

    public void enviarNotificacionUsuarioCreacion(Persona persona, ContenidoCarta contenidoCarta);

    public void enviarNotificacionSolicitudConstanciaCreacion(TramiteDocumentoAcademico tramiteDocumentoAcademico, ContenidoCarta contenidoCarta);

    public void enviarNotificacionTicketHelpDesk(Persona persona, ContenidoCarta contenidoCarta);

    public void enviarNotificacionAulaReservaAceptado(String nombre, String email, ContenidoCarta contenidoCarta);

    public void enviarNotificacionAulaReservaRechazado(String nombre, String email, ContenidoCarta contenidoCarta);

    void enviarCorreoAccesoEspecial(String correo, Usuario usuarioBD, String contraseña, String asunto, ContenidoCarta contenidoCarta);

}
