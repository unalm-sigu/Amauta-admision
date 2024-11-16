package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;

@Getter
@Setter
@NoArgsConstructor
public class CursoCicloGrupoDTO {

    private CursoCicloAcademico cursoCiclo;
    private GrupoHorasNivelacion grupoHoras;

    public CursoCicloGrupoDTO(CursoCicloAcademico cursoCiclo, GrupoHorasNivelacion grupoHoras) {
        this.cursoCiclo = cursoCiclo;
        this.grupoHoras = grupoHoras;
    }

}
