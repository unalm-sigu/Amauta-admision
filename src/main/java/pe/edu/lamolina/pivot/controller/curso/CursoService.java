package pe.edu.lamolina.pivot.controller.curso;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface CursoService {

    List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos);

    void save(Curso curso, Usuario usuario);

    Curso find(Long id);

    void cambiarEstadoCurso(Curso curso);

}
