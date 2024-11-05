package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.CicloAcademico;

@NoArgsConstructor
@Getter
@Setter
public class FiltroHistoricoCargaAcademicaDTO {

    private Long departamento;
    private Long docente;
    private List<CicloAcademico> cicloAcademicos;
    private Long facultad;

    public boolean hasCiclo() {
        if (this.cicloAcademicos == null || this.cicloAcademicos.isEmpty()) {
            return false;
        }
        
        return true;
    }

}
