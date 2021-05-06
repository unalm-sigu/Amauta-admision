package pe.edu.lamolina.amauta.zelper.mail;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;

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
    public void enviarNotificacionAulaReservaAceptado(String nombre, String email, ContenidoCarta contenidoCarta) {

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
    public void enviarNotificacionAulaReservaRechazado(String nombre, String email, ContenidoCarta contenidoCarta) {

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

    @Override
    public void enviarNotificacionReunionConsejero(ReunionAlumnoConsejero reunionAlumnoConsejero, Consejero consejero, ContenidoCarta contenidoCarta) {

        try {

            AgendaConsejero agendaConsejero = reunionAlumnoConsejero.getAgendaConsejero();

            String contenido = contenidoCarta.getContenido();

            Alumno alumno = reunionAlumnoConsejero.getAlumnoConsejero().getAlumno();
            Persona alumnoPersona = alumno.getPersona();
            Persona consejeroPersona = consejero.getColaborador().getPersona();

            contenido = contenido.replace(VariableContenidoEnum.NOMBRE_PERSONA.getValue(), alumnoPersona.getApellidosNombres());
            contenido = contenido.replace(VariableContenidoEnum.ESTIMADO.getValue(), alumnoPersona.getEstimado());
            contenido = contenido.replace(VariableContenidoEnum.HORA_REUNION_CONSEJERO.getValue(), agendaConsejero.getHora().getDescripcion());
            contenido = contenido.replace(VariableContenidoEnum.PROFESOR.getValue(), consejeroPersona.getApellidosNombres());

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(agendaConsejero.getFecha());

            contenido = contenido.replace(VariableContenidoEnum.FECHA_REUNION_CONSEJERO.getValue(), strDate);
            contenido = contenido.replace(VariableContenidoEnum.CUERPO_MENSAJE.getValue(), agendaConsejero.getCuerpo());

            Context ctx = new Context();
            ctx.setVariable("contenido", contenido);

            InternetAddress ie = new InternetAddress();
            ie.setPersonal("TUTORÍA - DOCENTES");
            ie.setAddress(consejeroPersona.getEmailCompania());

            MailMessage mail = new MailMessage();
            mail.setContext(ctx);
            mail.setTemplate("mail/mailReunionConsejero");
            mail.setSubject(contenidoCarta.getNombre().concat(": ").concat(agendaConsejero.getAsunto()));
            //mail.setDestinatarios(new String[]{email});
            mail.setDestinatarios(new String[]{alumnoPersona.getEmailCompania()}); //emailAlumno
            mail.setFrom(ie);
            mailerConnector.sendMailAgendaConsejero(mail);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(MailerServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
