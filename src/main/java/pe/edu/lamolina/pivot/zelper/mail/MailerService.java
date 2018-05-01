package pe.edu.lamolina.pivot.zelper.mail;

import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface MailerService {

    public void enviarNotificacionUsuarioCreacion(Persona persona, ContenidoCarta contenidoCarta);

    public void enviarNotificacionSolicitudConstanciaCreacion(TramiteDocumentoAcademico tramiteDocumentoAcademico, ContenidoCarta contenidoCarta);
}
