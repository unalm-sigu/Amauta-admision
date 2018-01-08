package pe.edu.lamolina.pivot.zelper.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.mail.connector.MailMessage;
import pe.edu.lamolina.pivot.zelper.mail.connector.MailerConnector;

@Service
@Transactional
public class MailerServiceImp implements MailerService {

    @Autowired
    MailerConnector mailerConnector;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void enviarNotificacionUsuarioCreacion(Persona persona, String asunto, String mensaje) {

        Context ctx = new Context();
        ctx.setVariable("mensaje", mensaje);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailUsuarioCreacion");
        mail.setSubject(asunto);
        mail.setDestinatarios(new String[]{persona.getEmail()});
        //mail.setDestinatarios(new String[]{"bladymir@albatross.pe"});
        mailerConnector.sendMail(mail);
    }

}
