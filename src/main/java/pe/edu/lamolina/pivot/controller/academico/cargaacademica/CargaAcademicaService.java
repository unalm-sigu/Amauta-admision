package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;

public interface CargaAcademicaService {

    List<TipoEvaluacion> allTipoEvaluacion();

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente);

    PlanCalificacion findPlanCalificacion(Long idPlanCalificacion);

    Curso findCurso(Long idCurso);

    Seccion findSeccion(Long idGrupoSeccion);

    GrupoSeccion findGrupo(Long idGrupoSeccion);

    List<EvaluacionPlan> allEvaluacionPlanByDynatable(DynatableFilter filter, Long idPlanCalificacion);

}
