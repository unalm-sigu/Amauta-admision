package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngresantesNivelacionCarreraDTO {
    private String carrera;
    private String facultad;
    private String matricula;
    private String ingresante;
    private String curso;
    private String notaInicial;
    private String notaFinal;
    private String estadoCurso;
    private String estadoAlumno;
    private String docente;
    private String porcentajeAsistencia;

}
