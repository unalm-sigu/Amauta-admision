package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;

public interface MatriculaCursoDAO extends Crud<MatriculaCurso> {

    MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> findByCursoCiclo(Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> allByMatriculaResumen(MatriculaResumen resumen);

    List<MatriculaCurso> allByAlumno(Long idAlumno);

}
