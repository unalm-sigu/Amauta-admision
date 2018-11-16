package pe.edu.lamolina.pivot.controller.horariocachimbo.curso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface HorarioCachimboCursoService {

    List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    void delete(CursoCachimbos cursoCachimbos);

    List<Curso> allCursoByName(String nombre);

    List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio);

    void addCurso(CursoCachimbos cursoCachimbos);

    List<CarreraCursoCachimbo> allCarrera(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico);

    Map<Long, Map<Long, HorarioCachimbos>> allSeccionHorarioCachimbos(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico);

    void fillGrupoSeccion(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico);

    String getClave(Seccion seccion);

    void updateSeccionCursoCachimbo(CarreraCursoCachimbo carreraCursoCachimbo, Usuario usuario);

    List<SeccionCursoCachimbos> allCursoCachimbos(List<CursoCachimbos> cursoCachimbos);
}
