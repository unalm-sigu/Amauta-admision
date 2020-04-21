package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;

public interface AlumnoCursoSimultaneoDAO extends EasyDAO<AlumnoCursoSimultaneo> {

    AlumnoCursoSimultaneo findByAlumnoCursoCurriculaCurso(AlumnoCursoCurricula alumnoCursoCurricula, Curso curso);

    void deleteAllByAlumno(Alumno alumno);
}
