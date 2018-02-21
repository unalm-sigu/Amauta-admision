package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface SistemaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    ObjectNode allTipoEvaluacionJson();

    List<SistemaNotas> allSistemasNotas();

    void saveSistemaCalifica(PlanCalificacion planCalificacion, DataSessionPivot ds);

    List<PlanCalificacion> allPlanesCalificacionByDynatable(DynatableFilter dynatableFilter, DataSessionPivot ds);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    void changeStatePlanCalificacion(Long idPLanCalificacion, String observacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    void changeStatePlanCalificacion(Long idPLanCalificacion, EstadoPlanCalificaEnum estadiPlanCalificaEnum);

    void asignarCurso(Long idCurso, Long idPlanCalificacion, DataSessionPivot ds);

    void desasignarCurso(Long idPlanCurso, Long idPersona);

    List<Curso> allActiveCursosByPlan(PlanCalificacion planCalificacion);

    List<PlanCalificacionCurso> allPlanCalificacionCursosByFilterDyna(DynatableFilter dynatableFilter, PlanCalificacion planCalificacion);

    DepartamentoAcademico buscarDepartamento(Long idDepartamento, DataSessionPivot ds);

}
