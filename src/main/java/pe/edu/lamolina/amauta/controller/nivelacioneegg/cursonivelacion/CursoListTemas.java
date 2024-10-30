package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Curso;

@Getter
@Setter
@NoArgsConstructor
public class CursoListTemas {

    private Curso curso;
    private List<Long> ids;
    
}
