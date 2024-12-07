package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatriculablesResumen {

    private Long inscritos;
    private Long pendientes;

    public MatriculablesResumen(Long inscritos, Long pendientes) {
        this.inscritos = inscritos;
        this.pendientes = pendientes;
    }

}
