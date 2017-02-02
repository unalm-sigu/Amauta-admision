package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

public interface CursoDAO extends Crud<Curso> {

    List<Curso> allForSistemaCalificacion(String nombre, Long idDepartamentoAca, Long planCalificacion, Long idCiclo);

    List<Curso> allByDynatable(DynatableFilter filter, Long planCalificacion, Long idDepartamentoAcademico);

    Curso find(Long idCurso);

    List<Curso> allByPlan(PlanCalificacion plan);
}
