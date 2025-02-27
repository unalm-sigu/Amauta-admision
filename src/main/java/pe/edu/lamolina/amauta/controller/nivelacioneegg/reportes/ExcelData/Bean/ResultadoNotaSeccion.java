package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResultadoNotaSeccion {
    
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
    
    
}
