package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoNotaSeccion;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface ReporteEGService {

    List<ResultadoNotaSeccion> allResultadoNotaSeccionByCiclo(CicloAcademico cicloAcademico);

}
