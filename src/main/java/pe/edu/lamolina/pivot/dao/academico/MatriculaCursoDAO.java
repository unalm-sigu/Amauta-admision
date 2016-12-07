package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;

public interface MatriculaCursoDAO extends Crud<MatriculaCurso> {

    MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo);

}
