package pe.edu.lamolina.amauta.controller.academico.infoacademico.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.calificacion.TemaExamen;

@Getter
@Setter
@NoArgsConstructor
public class AlumnoCursoCicloDTO {

    private TemaExamen temaExamen;
    private Curso curso;
    private CicloAcademico ciclo;
    private String nota;
    private Boolean aprobado;

    public AlumnoCursoCicloDTO(TemaExamen temaExamen, Curso curso, CicloAcademico ciclo, String nota) {
        this.temaExamen = temaExamen;
        this.curso = curso;
        this.ciclo = ciclo;
        this.nota = nota;
    }

}
