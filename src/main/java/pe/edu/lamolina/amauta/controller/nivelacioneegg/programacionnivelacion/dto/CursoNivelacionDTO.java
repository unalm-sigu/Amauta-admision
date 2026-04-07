package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

import java.util.List;

@Getter
@Setter
public class CursoNivelacionDTO {

    private GrupoNivelacion grupoNivelacion;
    private Integer horasSemanales;
    private Integer semanasDictado;
    private List<HorarioCurso> horarios;
    private Boolean grupoModificable;

    public CursoNivelacionDTO() {
        this.grupoModificable = true;
    }
}
