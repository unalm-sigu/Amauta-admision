package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;

public interface MatriculaCursoDAO extends EasyDAO<MatriculaCurso> {

    MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> findByCursoCiclo(Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> allByMatriculaResumen(MatriculaResumen resumen);

    List<MatriculaCurso> allByAlumno(Long idAlumno);

}
