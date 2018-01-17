package pe.edu.lamolina.pivot.zelper.pdf;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.DocumentoPdfEnum;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Service
public class PdfGeneratorImp implements PdfGenerator {

    private final String PDF_CSS = "public/pdf/css/pdf.css";
    private final String PDF_SAVE_PATH = Constantine.TMP_DIR;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String generateDocument(PdfContent pdfContent) {
        return generateDocument(pdfContent, null);
    }

    @Override
    public String generateDocument(PdfContent pdfContent, String subFolder) {
        logger.debug("Entro a generar documento pdf");
        Document docPDF = new Document(PageSize.A4);
        docPDF.addCreationDate();
        docPDF.addCreator("albatross.pe");

        DocumentoPdfEnum documentoPdfEnum = pdfContent.getDocumentPdfEnum();

        pdfContent.setSubject(documentoPdfEnum.getSubject());
        pdfContent.setTitle(documentoPdfEnum.getTitle());
        pdfContent.setTemplate(documentoPdfEnum.getFileTemplate());

        docPDF.addTitle(documentoPdfEnum.getTitle());
        docPDF.addSubject(documentoPdfEnum.getSubject());

        String resultado = "";
        String filePath = null;
        try {
            filePath = documentPdfAdmisionFilename(documentoPdfEnum, subFolder);
            logger.debug("Generara el documento {}", filePath);
            File filex = new File(filePath);

            FileOutputStream fileout;
            fileout = new FileOutputStream(filex);
            PdfWriter writer = PdfWriter.getInstance(docPDF, fileout);

            docPDF.open();
            docPDF.add(new Chunk(""));

            String htmlContent = this.templateEngine.process(pdfContent.getTemplate(), pdfContent.getContext());

            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode node = cleaner.clean(htmlContent);
            resultado = cleaner.getInnerHtml(node);

            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            htmlContext.setImageProvider(new PdfImageProvider());

            CSSResolver cssResolver = new StyleAttrCSSResolver();
            //    logger.debug("La ruta del css es {}", this.getClass().getResource(PDF_CSS).getFile());
            /*
            InputStream csspathtest = new FileInputStream(new File(this.getClass().getResource(PDF_CSS).getFile()));
            CssFile cssfiletest = XMLWorkerHelper.getCSS(csspathtest);
            cssResolver.addCss(cssfiletest);
             */
            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
                    new HtmlPipeline(htmlContext, new PdfWriterPipeline(docPDF, writer)));

            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser p = new XMLParser(worker);

            if (resultado != null) {
                p.parse(new StringReader(resultado));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        docPDF.close();
        logger.debug("Se genero el documento pdf {}", filePath);
        return filePath;
    }

    private String documentPdfAdmisionFilename(DocumentoPdfEnum docPdfEnum, String subFolder) {
        File folder = new File(PDF_SAVE_PATH + (StringUtils.isEmpty(subFolder) ? "" : File.separator + subFolder));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String time = TypesUtil.getUnixTime().toString();
        return folder + File.separator + File.separator + time + "_" + docPdfEnum.getName().replace(' ', '_') + "_" + ".pdf";
    }

    @Override
    public String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate) {

        Document document = new Document();
        OutputStream outputStream = null;
        String filePath = PDF_SAVE_PATH + outputStreamStr + ".pdf";
        try {
            File filex = new File(filePath);
            outputStream = new FileOutputStream(filex);

            List<InputStream> pdfs = new ArrayList<InputStream>();
            for (String pdfStr : pdfFilesStr) {
                pdfs.add(new FileInputStream(pdfStr));
            }
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {

                    Rectangle rectangle = pdfReader.getPageSizeWithRotation(1);
                    document.setPageSize(rectangle);
                    document.newPage();

                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    switch (rectangle.getRotation()) {
                        case 0:
                            cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                            break;
                        case 90:
                            cb.addTemplate(page, 0, -1f, 1f, 0, 0, pdfReader
                                    .getPageSizeWithRotation(1).getHeight());
                            break;
                        case 180:
                            cb.addTemplate(page, -1f, 0, 0, -1f, 0, 0);
                            break;
                        case 270:
                            cb.addTemplate(page, 0, 1.0F, -1.0F, 0, pdfReader
                                    .getPageSizeWithRotation(1).getWidth(), 0);
                            break;
                        default:
                            break;
                    }
                    if (paginate) {
                        cb.beginText();
                        cb.getPdfDocument().getPageSize();
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return filePath;
    }

}
