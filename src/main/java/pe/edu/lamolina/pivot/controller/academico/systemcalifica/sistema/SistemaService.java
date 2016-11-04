package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion);

    List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

}
