package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface ReporteEGService {

    List<ResultadoReporteView> allNotasGeneralByCiclo(CicloAcademico cicloAcademico);

    List<ResultadoReporteView> allNotasBySeccionAndCiclo(CicloAcademico cicloAcademico, String idSeccion);

    List<ResultadoReporteView> allAsistenciaBySeccionAndCiclo(CicloAcademico cicloAcademico, String codSeccion);

    List<ResultadoReporteView> allIngresantesDesaprobadosByCiclo(CicloAcademico cicloAcademico);

}
