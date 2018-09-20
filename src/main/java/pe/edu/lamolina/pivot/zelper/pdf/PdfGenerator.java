package pe.edu.lamolina.pivot.zelper.pdf;

import com.itextpdf.text.Rectangle;
import java.util.List;

public interface PdfGenerator {

    String generateDocument(PdfContent pdfContent, Rectangle rectangle);

    String generateDocument(PdfContent pdfContent, String subFolder, Rectangle pageSize);

    String generateDocument(PdfContent pdfContent, String subFolder);

    String generateDocument(PdfContent pdfContent);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);

}
