package pe.edu.lamolina.amauta.zelper.pdf.pdfHtml;

import org.thymeleaf.context.Context;

public class PdfContent {

    private Context context;

    private String documentPdfEnumName;

    private String template;

    private String title;

    private String subject;

    private String nombre;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDocumentPdfEnumName() {
        return documentPdfEnumName;
    }

    public void setDocumentPdfEnumName(String documentPdfEnumName) {
        this.documentPdfEnumName = documentPdfEnumName;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
