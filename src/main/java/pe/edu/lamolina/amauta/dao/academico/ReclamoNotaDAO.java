package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.ReclamoNota;

public interface ReclamoNotaDAO extends EasyDAO<ReclamoNota> {

    List<ReclamoNota> allByFilter(Evaluacion evaluacion);

    void deleteByEvaluacion(Evaluacion evaluacion);

}
