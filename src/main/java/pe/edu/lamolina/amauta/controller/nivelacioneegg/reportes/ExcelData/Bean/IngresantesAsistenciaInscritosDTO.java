package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngresantesAsistenciaInscritosDTO {
    
    private String curso;
    private Integer total;
    private Integer mayorIgual50Asistencia;
    private Integer menora50Asistencia;
    private Integer zeroAsistencia;
    
    
}
