package pe.edu.lamolina.amauta.controller.academico.acta.tipoevaluacion;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

import java.util.List;

public interface TipoEvalucionService {
    List<TipoEvaluacion> allByDynatable(DynatableFilter filter);
    void save(TipoEvaluacion tipo);
    void actualizarTipoEvaluacion(TipoEvaluacion tipo);
}
