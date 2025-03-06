package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlumnosNivelacionResumen {

    private Long noMatriculados;
    private Long matriculados;
    private Long inhabilitados;

    public AlumnosNivelacionResumen(Long noMatriculados, Long matriculados, Long inhabilitados) {
        this.noMatriculados = noMatriculados;
        this.matriculados = matriculados;
        this.inhabilitados = inhabilitados;
    }

}
