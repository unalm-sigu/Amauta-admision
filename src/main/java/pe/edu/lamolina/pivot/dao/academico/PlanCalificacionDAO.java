package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

public interface PlanCalificacionDAO extends Crud<PlanCalificacion> {

    List<PlanCalificacion> allByDynatable(DynatableFilter filter, DepartamentoAcademico dpto);

    PlanCalificacion find(Long idPlanCalificacion);

    Long maxNumeroCorrelativoPlanCalifica(Long idDepartamentoAcademico);

}
