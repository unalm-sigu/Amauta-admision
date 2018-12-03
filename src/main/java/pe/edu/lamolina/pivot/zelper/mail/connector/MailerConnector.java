package pe.edu.lamolina.pivot.zelper.mail.connector;

public interface MailerConnector {

    void sendMail(MailMessage mail);

    void sendMailHelpDesk(MailMessage mail);

}
