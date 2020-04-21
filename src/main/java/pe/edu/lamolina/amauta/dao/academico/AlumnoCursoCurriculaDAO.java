package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;

public interface AlumnoCursoCurriculaDAO extends EasyDAO<AlumnoCursoCurricula> {

    List<AlumnoCursoCurricula> allObligatoriosByAlumno(Alumno alumno);

    List<AlumnoCursoCurricula> allByAlumnoAprob(Alumno alumno, CicloAcademico ciclo);

    List<AlumnoCursoCurricula> allCiclosAlumno(Alumno alumno);

    List<AlumnoCursoCurricula> allByAlumnoCursosCurricula(Alumno alumno, List<CursoCurricula> cursosCurricula);

    void deleteAllByAlumno(Alumno alumno);

    AlumnoCursoCurricula findByAlumnoCurso(Alumno alumno, Curso curso);

    void updateEstado(AlumnoCursoCurricula alumnoCursoCurricula);

    List<AlumnoCursoCurricula> allByAlumnoCicloRegularAct(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCursoCurricula> allByAlumnosCurso(List<Alumno> alumnos, Curso curso);

    List<AlumnoCursoCurricula> allByAlumnosApr(List<Alumno> alumnos);

    List<AlumnoCursoCurricula> allByAlumnoApro(Alumno alumnoBD);

    List<AlumnoCursoCurricula> allByAlumnoCursosOpcional(Alumno alumno);

    List<AlumnoCursoCurricula> allByAlumnoComodin(Alumno alumno);

    List<AlumnoCursoCurricula> allByAlumnos(List<Alumno> alumnos);

    List<AlumnoCursoCurricula> allByAlumnoAndModalidad(Alumno alumno, DynatableFilter filter);

    List<AlumnoCursoCurricula> all(Alumno alumno);

    List<AlumnoCursoCurricula> allDynaTable(Alumno alumno, DynatableFilter filter);

    List<AlumnoCursoCurricula> allByAlumno(Alumno alumno);

    void updateColumns(AlumnoCursoCurricula alumnoCursoCurricula, String... columns);
}
