package pe.edu.lamolina.amauta.zelper.mail.connector;

public interface MailerConnector {

    void sendMail(MailMessage mail);

    void sendMailHelpDesk(MailMessage mail);

}
