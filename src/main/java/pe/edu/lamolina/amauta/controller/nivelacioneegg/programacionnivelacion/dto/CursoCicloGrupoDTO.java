package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;

@Getter
@Setter
@NoArgsConstructor
public class CursoCicloGrupoDTO {

    private CursoCicloAcademico cursoCiclo;
    private PlantillaNivelacion plantilla;

    public CursoCicloGrupoDTO(CursoCicloAcademico cursoCiclo, PlantillaNivelacion plantilla) {
        this.cursoCiclo = cursoCiclo;
        this.plantilla = plantilla;
    }

}
