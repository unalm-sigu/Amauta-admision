package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto;

import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

@Getter
@Setter
public class BuscarCruceDTO {

    private CursoCicloAcademico cursoCiclo;
    private GrupoHorasNivelacion grupoHoras;
    private AlumnoNivelacion alumnoNivelacion;
}
