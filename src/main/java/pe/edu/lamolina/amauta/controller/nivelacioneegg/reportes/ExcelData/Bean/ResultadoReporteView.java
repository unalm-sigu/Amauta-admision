package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;

@Setter
@Getter
public class ResultadoReporteView {

    private String codCurso;
    private String curso;
    private String docente;
    private String seccion;
    private String ciclo;
    private String matricula;
    private String apellidosNombre;
    private BigDecimal evaluacionParcial1;
    private BigDecimal evaluacionParcial2;
    private BigDecimal examenFinal;
    private BigDecimal promedioFinal;
    private String condicion;
    private BigDecimal porcentajeAsistencia;
    private List<AsistenciaNivelacion> asistencias;

}
