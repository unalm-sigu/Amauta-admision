package pe.edu.lamolina.pivot.zelper.pdf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface PdfGenerator {

    String generateDocument(PdfContent pdfContent, String subFolder);

    String generateDocument(PdfContent pdfContent);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);

}
