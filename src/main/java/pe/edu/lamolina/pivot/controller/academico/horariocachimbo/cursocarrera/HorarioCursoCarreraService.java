package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;

public interface HorarioCursoCarreraService {

    List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    void delete(CursoCachimbos cursoCachimbos);

    List<Curso> allCursoByName(String nombre);

    List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio);

    void addCurso(CursoCachimbos cursoCachimbos);

    List<CarreraCursoCachimbo> allCarrera(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico);

    Map<Long, Map<Long,HorarioCachimbos>> allSeccionHorarioCachimbos(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico);

}
