package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;

public interface MatriculaResumenDAO extends Crud<MatriculaResumen> {

    MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

}
