package pe.edu.lamolina.amauta.controller.docente.notasacademicas.reporte;

import com.itextpdf.text.Rectangle;
import java.util.List;

@Deprecated
public interface PdfActaNotasGenerator {

    String generateDocument(PdfActaNotasContent pdfContent, Rectangle rectangle);

    String generateDocument(PdfActaNotasContent pdfContent, String subFolder, Rectangle pageSize);

    String generateDocument(PdfActaNotasContent pdfContent, String subFolder);

    String generateDocument(PdfActaNotasContent pdfContent);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);

}
