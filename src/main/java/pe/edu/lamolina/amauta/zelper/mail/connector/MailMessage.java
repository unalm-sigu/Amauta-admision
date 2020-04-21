package pe.edu.lamolina.amauta.zelper.mail.connector;

import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.InternetAddress;
import org.thymeleaf.context.Context;

public class MailMessage {

    private InternetAddress from;

    private InternetAddress to;

    private InternetAddress co;

    private Context context;

    private String template;

    private String subject;

    private String[] destinatarios;

    private String[] destinatariosCC;

    private List<String> files;

    public InternetAddress getFrom() {
        return from;
    }

    public void setFrom(InternetAddress from) {
        this.from = from;
    }

    public InternetAddress getTo() {
        return to;
    }

    public void setTo(InternetAddress to) {
        this.to = to;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public InternetAddress getCo() {
        return co;
    }

    public void setCo(InternetAddress co) {
        this.co = co;
    }

    public String[] getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String[] destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String[] getDestinatariosCC() {
        return destinatariosCC;
    }

    public void setDestinatariosCC(String[] destinatariosCC) {
        this.destinatariosCC = destinatariosCC;
    }

    public List<String> getFiles() {
        return files;
    }

    public void addFile(String file) {
        if (this.files == null) {
            this.files = new ArrayList();
        }

        files.add(file);
    }

}
