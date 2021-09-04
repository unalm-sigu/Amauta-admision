package pe.edu.lamolina.amauta.controller.docente.notasacademicas.reporte;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Deprecated
public interface ReporteActaNotasService {

    List<String> reporteDeActaDeNotas(Long idDocenteSeccion, DataSessionPivot ds);

    String concatPDFs(List<String> pdfFilesStr, String outputStreamStr, boolean paginate);

}
