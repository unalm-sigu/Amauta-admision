package pe.edu.lamolina.amauta.zelper.mail;

import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface MailerService {

    public void enviarNotificacionUsuarioCreacion(Persona persona, ContenidoCarta contenidoCarta);

    public void enviarNotificacionAulaReservaAceptado(String estimado, String nombre, String email, ContenidoCarta contenidoCarta);

    public void enviarNotificacionAulaReservaRechazado(String estimado, String nombre, String email, ContenidoCarta contenidoCarta);

    void enviarCorreoAccesoEspecial(String correo, Usuario usuarioBD, String contraseña, String asunto, ContenidoCarta contenidoCarta);

    void enviarNotificacionReunionConsejero(ReunionAlumnoConsejero reunionAlumnoConsejero, Consejero consejero, ContenidoCarta contenidoCarta);

    public void enviarNotificacionUsuarioContrasena(String estimado, String nombre, String email, String pass);

}
