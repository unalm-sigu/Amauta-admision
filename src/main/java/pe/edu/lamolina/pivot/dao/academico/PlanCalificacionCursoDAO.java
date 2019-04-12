package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;

public interface PlanCalificacionCursoDAO extends EasyDAO<PlanCalificacionCurso> {

    PlanCalificacionCurso findByFilter(PlanCalificacion planCalificacion, Curso curso, EstadoEnum estadoEnum);

    List<PlanCalificacionCurso> allByFilter(PlanCalificacion planCalificacion, TipoCicloEnum tipoCicloEnum, Curso curso, EstadoEnum estadoEnum);

    List<PlanCalificacionCurso> allByFilterDyna(DynatableFilter filter, PlanCalificacion planCalificacion, EstadoEnum estadoPlanCurdo);

    List<PlanCalificacionCurso> allActivosByPLanes(List<PlanCalificacion> planes);

}
