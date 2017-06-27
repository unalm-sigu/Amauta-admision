package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;

public interface ReclamoNotaDAO extends Crud<ReclamoNota> {

    List<ReclamoNota> allByFilter(Evaluacion evaluacion);

    void deleteByEvaluacion(Evaluacion evaluacion);

}
