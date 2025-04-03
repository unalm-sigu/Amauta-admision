package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngresantesExamenAdmisionDTO {

    private String materia;
    private Integer aprobados;
    private BigDecimal porcAprobados;
    private Integer desaprobados;
    private BigDecimal porcDesaprobados;

}
