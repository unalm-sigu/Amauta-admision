package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    ObjectNode allTipoEvaluacionJson();

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion, DataSessionPivot ds);

    List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter, DepartamentoAcademico dpto);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    void changeStatePlanCalificacion(Long idPLanCalificacion, String observacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    List<Curso> allCursosByPlanCalifica(DynatableFilter dynatableFilter, Long idPlanCalificacion, Long idDepartamentoAcademico);

    void asignarCurso(Long idCurso, Long idPlanCalificacion, Long idPersona);

    void desasignarCurso(Long idPlanCurso, Long idPersona);

    List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion);

    List<PlanCalificacionCurso> allPlanCalificacionCursosByFilterDyna(DynatableFilter dynatableFilter, PlanCalificacion planCalificacion);

}
