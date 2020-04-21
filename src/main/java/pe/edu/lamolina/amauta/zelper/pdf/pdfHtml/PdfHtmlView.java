package pe.edu.lamolina.amauta.zelper.pdf.pdfHtml;

import com.google.common.base.Strings;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.pdf.PdfImageProvider;

@Component
public class PdfHtmlView extends AbstractPdfHtmlView {

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document doc,
            PdfWriter writer, HttpServletRequest hsr, HttpServletResponse response) throws Exception {

        Context ctx = new Context();

        ctx.setVariables(model);

        PDFFormatoEnum data = (PDFFormatoEnum) model.get("formatoEnum");
        if (data == null) {
            throw new PhobosException("PDFFormatoEnum NO ESPECIFICADO.");
        }
        logger.debug("TYPE DOCUMENT GENERATE {}", data.getTitle());

        PdfContent pdfContent = new PdfContent();
        this.documentContent(pdfContent, data, ctx);
        this.documentProperty(doc, data);

        String resultado = "";
        String htmlContent = this.templateEngine.process(pdfContent.getTemplate(), pdfContent.getContext());

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(htmlContent);
        resultado = cleaner.getInnerHtml(node);

        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        htmlContext.setImageProvider(new PdfImageProvider());

        CSSResolver cssResolver = new StyleAttrCSSResolver();

        InputStream csspathtest = this.getClass().getResourceAsStream(GlobalConstantine.PDF_CSS);

        CssFile cssfiletest = XMLWorkerHelper.getCSS(csspathtest);
        cssResolver.addCss(cssfiletest);
        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(doc, writer)));
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser p = new XMLParser(worker);

        if (resultado != null) {
            p.parse(new StringReader(resultado));
        }

        String nombre = (String) model.get("nombrePdf");
        if (Strings.isNullOrEmpty(nombre)) {
            nombre = "untitle";
        }
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
    }

    private void documentProperty(Document documentPdf, PDFFormatoEnum data) {

        documentPdf.setPageSize(PageSize.A4);
        documentPdf.addAuthor("Albatross");
        documentPdf.addCreationDate();
        documentPdf.addCreator("Albatross");
        documentPdf.addTitle(data.getTitle());
        documentPdf.addSubject(data.getSubject());

    }

    private void documentContent(PdfContent pdfContent, PDFFormatoEnum data, Context ctx) {

        pdfContent.setContext(ctx);
        pdfContent.setNombre(data.getName());
        pdfContent.setSubject(data.getSubject());
        pdfContent.setTitle(data.getTitle());
        pdfContent.setTemplate(data.getFileTemplate());

    }

}
