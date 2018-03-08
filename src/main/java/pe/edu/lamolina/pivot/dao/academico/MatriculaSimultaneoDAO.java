package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;

public interface MatriculaSimultaneoDAO extends EasyDAO<MatriculaSimultaneo> {

    public List<MatriculaSimultaneo> allByMatriculaCurso(List<MatriculaCurso> matriculaCursos);

}
