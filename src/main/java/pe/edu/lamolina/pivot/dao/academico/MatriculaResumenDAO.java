package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.EstadoMatriculaCursoEnum;

public interface MatriculaResumenDAO extends EasyDAO<MatriculaResumen> {

    MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaResumen> allByCiclo(CicloAcademico ciclo);

    MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaCursoEnum estadoMatriculaCursoEnum);

}
