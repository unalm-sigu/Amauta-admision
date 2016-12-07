package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    ObjectNode allTipoEvaluacionJson() throws JSONException;

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion);

    List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter, DepartamentoAcademico dpto);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    void changeStatePlanCalificacion(Long idPLanCalificacion, String observacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    List<Curso> allCursosByPlanCalifica(DynatableFilter dynatableFilter, Long planCalificacion, Long idDepartamentoAcademico);

    void asignarCurso(Long idCurso, Long idPlanCalificacion, Long idPersona);

    void desasignarCurso(Long idCurso, Long idPlanCalificacion, Long idPersona);

}
