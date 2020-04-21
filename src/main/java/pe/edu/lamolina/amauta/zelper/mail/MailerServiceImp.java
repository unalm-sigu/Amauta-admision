package pe.edu.lamolina.amauta.zelper.mail;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.edu.lamolina.model.enums.VariableContenidoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.amauta.zelper.mail.connector.MailMessage;
import pe.edu.lamolina.amauta.zelper.mail.connector.MailerConnector;

@Service
@Transactional
public class MailerServiceImp implements MailerService {

    @Autowired
    MailerConnector mailerConnector;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void enviarNotificacionUsuarioCreacion(Persona persona, ContenidoCarta contenidoCarta) {
        String estimado = persona.esFemenino() ? "Estimada" : "Estimado";

        String contenido = contenidoCarta.getContenido();
        String banner = contenidoCarta.getImgUrl();

        contenido = contenido.replaceAll(VariableContenidoEnum.ESTIMADO.getValue(), estimado);
        contenido = contenido.replaceAll(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        contenido = contenido.replaceAll(VariableContenidoEnum.CORREO_CREADO.getValue(), persona.getEmailCompania());

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);
        ctx.setVariable("banner", banner);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailUsuarioCreacion");
        mail.setSubject("Creación de usuario");
        mail.setDestinatarios(new String[]{persona.getEmail()});
        //mail.setDestinatarios(new String[]{"bladymircch@gmail.com"});
        mailerConnector.sendMail(mail);
    }

    @Override
    public void enviarNotificacionSolicitudConstanciaCreacion(TramiteDocumentoAcademico tramiteDocumentoAcademico, ContenidoCarta contenidoCarta) {

        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), tramiteDocumentoAcademico.getPersonaContacto());

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailSolicitudConstancia");
        mail.setSubject("Solicitud de constancia");
        mail.setDestinatarios(new String[]{tramiteDocumentoAcademico.getEmail()});
//        mail.setDestinatarios(new String[]{"davd.1491@gmail.com"});
        mailerConnector.sendMail(mail);
    }

    @Override
    public void enviarNotificacionTicketHelpDesk(Persona persona, ContenidoCarta contenidoCarta) {

        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailHelpDesk");
        mail.setSubject(contenidoCarta.getNombre());
        //mail.setDestinatarios(new String[]{ colaborador.getPersona().getEmail()});
        mail.setDestinatarios(new String[]{"bladymircch@gmail.com"});
        mailerConnector.sendMailHelpDesk(mail);

    }

    @Override
    public void enviarNotificacionAulaReservaAceptado(String nombre,String email, ContenidoCarta contenidoCarta) {

        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), nombre);

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailReservaAula");
        mail.setSubject(contenidoCarta.getNombre());
        //mail.setDestinatarios(new String[]{email});
        mail.setDestinatarios(new String[]{"bladymircch@gmail.com"});
        mailerConnector.sendMailHelpDesk(mail);
    }

    @Override
    public void enviarNotificacionAulaReservaRechazado(String nombre,String email, ContenidoCarta contenidoCarta) {

        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), nombre);

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailReservaAula");
        mail.setSubject(contenidoCarta.getNombre());
        //mail.setDestinatarios(new String[]{email});
        mail.setDestinatarios(new String[]{"bladymircch@gmail.com"});
        mailerConnector.sendMailHelpDesk(mail);
    }

    
    //// PENDIENTE
    @Override
    public void enviarCorreoAccesoEspecial(String correo, Usuario usuarioBD, String contraseña, String asunto, ContenidoCarta contenidoCarta) {
        try {

            String contenido = contenidoCarta.getContenido();
            String estimado = usuarioBD.getPersona().esFemenino() ? "Estimada" : "Estimado";

            contenido = contenido.replaceAll(Pattern.quote(VariableContenidoEnum.ESTIMADO.getValue()), estimado);
            contenido = contenido.replaceAll(Pattern.quote(VariableContenidoEnum.NOMBRE_PERSONA.getValue()), usuarioBD.getPersona().getNombreCompleto());

            Context ctx = new Context();
            ctx.setVariable("mensaje", contenido);
            ctx.setVariable("username", usuarioBD.getUserDni());
            ctx.setVariable("password", contraseña);

            MailMessage mail = new MailMessage();
            mail.setContext(ctx);
            mail.setTemplate("mail/mailOtorgarAccesoEspecial");

            mail.setSubject(asunto);
            mail.setDestinatarios(new String[]{"seichi.jonda@tecsup.edu.pe"});
//            mail.setDestinatarios(new String[]{correo});

            InternetAddress internetAddress = new InternetAddress();
            internetAddress.setPersonal("UNALM - INTRANET");
            internetAddress.setAddress("no-responder@carrerasqueapasionan.pe");
            mail.setFrom(internetAddress);

            mailerConnector.sendMail(mail);

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
