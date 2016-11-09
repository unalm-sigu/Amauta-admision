package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion);

    List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    List<Curso> allCursosByPlanCalifica(DynatableFilter dynatableFilter, Long planCalificacion);

}
