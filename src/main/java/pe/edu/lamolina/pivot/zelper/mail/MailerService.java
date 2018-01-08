package pe.edu.lamolina.pivot.zelper.mail;

import pe.edu.lamolina.model.general.Persona;

public interface MailerService {

    public void enviarNotificacionUsuarioCreacion(Persona persona, String asunto, String mensaje);
}
