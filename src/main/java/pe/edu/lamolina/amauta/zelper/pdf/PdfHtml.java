package pe.edu.lamolina.amauta.zelper.pdf;

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
import org.apache.commons.lang3.StringUtils;
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

@Component
public class PdfHtml extends AbstractPdfHtml {

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String PATH_PDF_TEMPLATE = "pdf/";

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document documentPdf, PdfWriter writer, HttpServletRequest hsr, HttpServletResponse response) throws Exception {

        Context ctx = new Context();

        ctx.setVariables(model);

        String plantillas = (String) model.get("templatePdf");
        if (StringUtils.isBlank(plantillas)) {
            throw new PhobosException("Plantilla no especificada.");
        }

        String[] plantillasArray = plantillas.split(",");
        logger.debug("plantillas {}", plantillas);

        String nombre = (String) model.get("nombrePdf");

        if (StringUtils.isBlank(nombre)) {
            nombre = "untitle";
        }

        documentPdf.setPageSize(PageSize.A4);
        documentPdf.addAuthor("AgrariaLaMolina");
        documentPdf.addCreationDate();
        documentPdf.addCreator("AgrariaLaMolina");
        documentPdf.addTitle(nombre);
        documentPdf.addSubject(nombre);

        for (String plantilla : plantillasArray) {

            logger.debug("plantilla {}", plantilla);

            String htmlContent = this.templateEngine.process(templateResolver(plantilla), ctx);

            HtmlCleaner cleaner = new HtmlCleaner();

            TagNode node = cleaner.clean(htmlContent);

            String resultado = cleaner.getInnerHtml(node);

            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            htmlContext.setImageProvider(new PdfImageProvider());

            CSSResolver cssResolver = new StyleAttrCSSResolver();
            InputStream csspathtest = this.getClass().getResourceAsStream(GlobalConstantine.PDF_CSS);

            CssFile cssfiletest = XMLWorkerHelper.getCSS(csspathtest);
            cssResolver.addCss(cssfiletest);
            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(documentPdf, writer)));
            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser p = new XMLParser(worker);

            if (resultado != null) {
                p.parse(new StringReader(resultado));
            }

            if (plantillasArray.length>1) {

                documentPdf.newPage();

            }

        }

        response.setHeader("content-disposition", "attachment; filename=\"" + nombre + ".pdf\"");
        response.setHeader("content-filename", nombre + ".pdf");

    }

    private String templateResolver(String plantilla) {
        return PATH_PDF_TEMPLATE + plantilla;
    }

}
