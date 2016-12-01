package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;

public interface EvaluacionDAO extends Crud<Evaluacion> {

    List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion);

}
