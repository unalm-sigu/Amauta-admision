package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;

public interface MatriculaResumenDAO extends Crud<MatriculaResumen> {

    MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaResumen> allByCiclo(CicloAcademico ciclo);

    MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaCursoEnum estadoMatriculaCursoEnum);

}
