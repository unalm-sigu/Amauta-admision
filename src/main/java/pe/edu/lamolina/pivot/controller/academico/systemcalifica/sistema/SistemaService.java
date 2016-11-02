package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    List<SistemaNotas> allSistemasNotas();

}
