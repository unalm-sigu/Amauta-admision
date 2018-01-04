package pe.edu.lamolina.pivot.zelper.pdf;

import org.thymeleaf.context.Context;
import pe.edu.lamolina.model.enums.DocumentoPdfEnum;

/**
 *
 * @author Albatross
 */
public class PdfContent {

    private Context context;

    private DocumentoPdfEnum documentPdfEnum;

    private String template;

    private String title;

    private String subject;

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

    public DocumentoPdfEnum getDocumentPdfEnum() {
        return documentPdfEnum;
    }

    public void setDocumentPdfEnum(DocumentoPdfEnum documentPdfEnum) {
        this.documentPdfEnum = documentPdfEnum;
    }

}
