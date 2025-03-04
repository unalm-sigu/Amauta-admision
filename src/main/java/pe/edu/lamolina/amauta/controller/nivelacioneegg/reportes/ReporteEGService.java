package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoNotaSeccion;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface ReporteEGService {

    List<ResultadoNotaSeccion> allGeneralNotaSeccionByCiclo(CicloAcademico cicloAcademico);

    List<ResultadoNotaSeccion> allBySeccionAndCiclo(CicloAcademico cicloAcademico, String idSeccion);

}
