package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;

public interface CursoNivelacionService {

    List<Curso> allByDynatable(DynatableFilter filter);

    void save(Curso curso, DataSessionPivot ds);

}
