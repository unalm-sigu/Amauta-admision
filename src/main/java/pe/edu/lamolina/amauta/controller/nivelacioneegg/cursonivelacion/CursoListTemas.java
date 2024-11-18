package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Curso;

//@Getter
//@Setter
@NoArgsConstructor
public class CursoListTemas {

    private Curso curso;
    private List<Long> ids;

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

}
