package pe.edu.lamolina.pivot.zelper.mail.connector;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.edu.lamolina.pivot.config.DespliegueConfig;

@Service
public class MailerConnectorImp implements MailerConnector {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMail(MailMessage mail) {
        try {
            
            logger.info("Enviando email {}", mail.getSubject());
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            InternetAddress ie = new InternetAddress();
            ie.setPersonal("UNALM - Sistema de Inscripción de alumno");
            ie.setAddress("no-responder@lamolina.edu.pe");
            message.setFrom(ie);

            message.setSubject(mail.getSubject());
            message.setTo(mail.getDestinatarios());

            String[] copias = despliegueConfig.getCopias().split(" ");
            String[] emails = despliegueConfig.getEmails().split(" ");

            String[] destinatarios = despliegueConfig.getMailer() ? mail.getDestinatarios() : emails;
            for (String destinatario : destinatarios) {
                logger.info("\temail to {}", destinatario);
            }
            message.setTo(destinatarios);
            if (despliegueConfig.getMailer()) {
                for (String copia : copias) {
                    logger.info("\temail bcc {}", copia);
                }
                message.setBcc(copias);
            }

            this.attachFiles(message, mail.getFiles());

            String htmlContent = this.templateEngine.process(mail.getTemplate(), mail.getContext());
            message.setText(htmlContent, true);

            this.javaMailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachFiles(MimeMessageHelper message, List<String> files) throws MessagingException {
        if (files == null) {
            return;
        }
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (String filePath : files) {
            FileSystemResource file = new FileSystemResource(filePath);
            if (file.exists()) {
                logger.debug("File Exists: {}", file.getPath());
                message.addAttachment(file.getFilename(), file);
            }
        }
    }

}
