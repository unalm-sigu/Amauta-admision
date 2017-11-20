package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.TipoCurriculaEnum;

public interface CursoDAO extends Crud<Curso> {

    List<Curso> allForSistemaCalificacion(String nombre, Long idDepartamentoAca, PlanCalificacion planCalificacion, Long idCiclo);

    Curso find(Long idCurso);

    List<Curso> allByPlan(PlanCalificacion plan);

    List<Curso> allByPlanRegular(PlanCalificacion plan);

    List<Curso> allActiveByPlan(PlanCalificacion plan);

    Curso findByCode(String codigo);

    List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos);

    List<Curso> allByNombreFilter(String nombre, List<String> tiposCurriculaEnum, Integer limit);

    List<Curso> allByCodigo(String codigo);

}
