package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;

public interface MatriculaSimultaneoDAO extends EasyDAO<MatriculaSimultaneo> {

    List<MatriculaSimultaneo> allByMatriculaCurso(List<MatriculaCurso> matriculaCursos);

    List<MatriculaSimultaneo> allByMatriculaCurso(MatriculaCurso matriculaCurso);

    List<MatriculaSimultaneo> allByCicloAcademico(CicloAcademico cicloAcademico);

}
