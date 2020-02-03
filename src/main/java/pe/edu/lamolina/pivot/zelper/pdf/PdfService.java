package pe.edu.lamolina.pivot.zelper.pdf;

import java.util.List;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PdfService {

    List<String> reporteDeActaDeNotas(Long idDocenteSeccion, DataSessionPivot ds);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);
    
    //List<String> reporteProgramacionqq(CicloAcademico ciclo);

}
