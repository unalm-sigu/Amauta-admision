package pe.edu.lamolina.amauta.controller.docente.notasacademicas.reporte;

import java.util.List;
import org.thymeleaf.context.Context;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface ReporteActaNotasService {

    List<Context> reporteDeActaDeNotas(Long idDocenteSeccion, DataSessionPivot ds);

}
