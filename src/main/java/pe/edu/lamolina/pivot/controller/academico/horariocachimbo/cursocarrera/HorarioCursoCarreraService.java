package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;

public interface HorarioCursoCarreraService {

    List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    void delete(CursoCachimbos cursoCachimbos);

    void addCurso(CursoCachimbos cursoCachimbos);

     List<Curso> allCursoByName(String nombre);

}
