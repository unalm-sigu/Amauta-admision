package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngresantesMateriasNivelacionDTO {

    private String curso;
    private Integer inscritos;
    private Integer aprobados;
    private Integer desaprobados;
    private Integer sinNota;
}
