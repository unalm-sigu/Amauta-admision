package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
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

    List<AlumnoCursoCurricula> allByAlumnoCicloRegularAct(Alumno alumno);

    public List<AlumnoCursoCurricula> allByAlumnosCurso(List<Alumno> alumnos, Curso curso);

    public List<AlumnoCursoCurricula> allByAlumnosApr(List<Alumno> alumnos);

    public List<AlumnoCursoCurricula> allByAlumnoApro(Alumno alumnoBD);

    List<AlumnoCursoCurricula> allByAlumnoCursosOpcional(Alumno alumno);

    List<AlumnoCursoCurricula> allByAlumnoComodin(Alumno alumno);

    public List<AlumnoCursoCurricula> allByAlumnos(List<Alumno> alumnos);
}
