package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;

public interface EvaluacionExpandidaDAO extends Crud<EvaluacionExpandida> {

    List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion);

    void deleteByEvaluacionParent(Long idEvaluacionParent);

    List<EvaluacionExpandida> allByEvaluacionSeccion(EvaluacionSeccion evalSecc);

}
