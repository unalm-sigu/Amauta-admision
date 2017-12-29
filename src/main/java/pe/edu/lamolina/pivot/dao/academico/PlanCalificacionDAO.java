package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.PlanCalificacion;

public interface PlanCalificacionDAO extends EasyDAO<PlanCalificacion> {

    List<PlanCalificacion> allByDynatable(DynatableFilter filter, DepartamentoAcademico dpto);

    PlanCalificacion find(Long idPlanCalificacion);

    Long maxNumeroCorrelativoPlanCalifica(Long idDepartamentoAcademico);

}
