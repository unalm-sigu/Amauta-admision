package pe.edu.lamolina.amauta.zelper.pdf;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface PdfService {

    List<String> reporteDeActaDeNotas(Long idDocenteSeccion, DataSessionPivot ds);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);
    
    //List<String> reporteProgramacionqq(CicloAcademico ciclo);

}
