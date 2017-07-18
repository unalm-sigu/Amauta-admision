package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

public interface EvaluacionExpandidaDAO extends Crud<EvaluacionExpandida> {

    List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup);

    List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup, EstadoEnum estadoEnum);

    void deleteByEvaluacionParent(Long idEvaluacionParent);

    List<EvaluacionExpandida> allByEvaluacionSeccion(EvaluacionSeccion evalSecc);

    List<EvaluacionExpandida> allByGpoSeccionPlan(GrupoSeccion gpoSeccion, PlanCalificacion plan);

    //List<EvaluacionExpandida> allByGpoSeccion(GrupoSeccion grupoSeccion);

}
