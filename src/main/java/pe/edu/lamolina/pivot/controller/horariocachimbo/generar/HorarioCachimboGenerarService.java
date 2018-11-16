package pe.edu.lamolina.pivot.controller.horariocachimbo.generar;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface HorarioCachimboGenerarService {

    ModalidadEstudio findModalidadPregrado();

    List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    void delete(HorarioCachimbos horarioCachimbos, CicloAcademico ciclo, Usuario usuario);

    void delete(HorarioCachimboForm form, CicloAcademico ciclo, Usuario usuario);

    List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico, Long horario);

    List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio);

    List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    List<HorarioCachimbos> allHorarioCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    List<SeccionHorarioCachimbos> allSeccionHorarioCachimbosByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico);

    String getClave(String codigo, List<SeccionHorarioCachimbos> shcHorario);

    List<Dia> allDia();

    List<Hora> allHora();

    List<HorarioSeccion> allSeccionHorarioCachimbosByHorarioCachimbos(HorarioCachimbos horario);

    void generar(CicloAcademico cicloAcademico, ModalidadEstudio modalidad, DataSessionPivot ds);

    String getHoraSeccion(SeccionHorarioCachimbos shc);

    void addAlumno(AlumnoHorario alumno);

    List<AlumnoHorario> allAlumnoHorarioByHorario(HorarioCachimbos horario);

    List<CursoCachimbos> allCursoCachimbosByHorario(HorarioCachimbos horario, CicloAcademico cicloAcademico);

    void permutarUnico(
            int ordenCurso, int ordenSeccion,
            List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones,
            Map<String, String> mapHorasDias, List<Seccion> horarioTempo, List<List<Seccion>> horariosCarrera,
            Map<Long, Seccion> mapSeccionesAlumno, Map<Long, Curso> mapCursosAlumno);

    void generarHorario(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds, List<AlumnoHorario> alumnos);

    List<GrupoSeccion> allGrupoSeccionByHorario(HorarioCachimbos horario, CicloAcademico cicloAcademico);

}
