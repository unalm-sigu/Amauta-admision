package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCicloEnum;

public interface PlanCalificacionCursoDAO extends Crud<PlanCalificacionCurso> {

    PlanCalificacionCurso findByFilter(PlanCalificacion planCalificacion, Curso curso, EstadoEnum estadoEnum);

    List<PlanCalificacionCurso> allByFilter(PlanCalificacion planCalificacion, TipoCicloEnum tipoCicloEnum, Curso curso, EstadoEnum estadoEnum);

    List<PlanCalificacionCurso> allByFilterDyna(DynatableFilter filter, PlanCalificacion planCalificacion, EstadoEnum estadoPlanCurdo);

}
