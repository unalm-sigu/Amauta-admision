package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlumnoPersonalizadoDTO {

    private String matricula;
    private String dni;
    private String alumno;
    private String carrera;
    private String situacion;
    private String ciclo;
    private String foto;

}
