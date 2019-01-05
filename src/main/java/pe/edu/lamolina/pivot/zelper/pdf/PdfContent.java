package pe.edu.lamolina.pivot.zelper.pdf;

import org.thymeleaf.context.Context;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;

public class PdfContent {

    private Context context;

    private TipoPdfEnum tipoPdfEnum;

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

    public TipoPdfEnum getTipoPdfEnum() {
        return tipoPdfEnum;
    }

    public void setTipoPdfEnum(TipoPdfEnum tipoDocumentoPdfEnum) {
        this.tipoPdfEnum = tipoDocumentoPdfEnum;
    }

}
