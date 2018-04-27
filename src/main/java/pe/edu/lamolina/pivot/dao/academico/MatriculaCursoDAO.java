package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;

public interface MatriculaCursoDAO extends EasyDAO<MatriculaCurso> {

    MatriculaCurso findByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> findByCursoCiclo(Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> allByMatriculaResumen(MatriculaResumen resumen);

    List<MatriculaCurso> allByMatriculaResumen(List<MatriculaResumen> resumenes);

    List<MatriculaCurso> allByMatriculaResumenCurso(List<MatriculaResumen> resumenes, Curso curso);

    List<MatriculaCurso> allByAlumno(Long idAlumno);

    List<MatriculaCurso> allByCursoCiclo(Curso curso, CicloAcademico ciclo);

    List<MatriculaCurso> allByCiclo(CicloAcademico ciclo);

    List<MatriculaCurso> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaCurso> allActivoByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico);

    List<MatriculaCurso> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens);

    List<MatriculaCurso> allByAlumnosCursosCiclo(List<Alumno> alumnos, List<Curso> cursos, CicloAcademico cicloAcademico);

}
