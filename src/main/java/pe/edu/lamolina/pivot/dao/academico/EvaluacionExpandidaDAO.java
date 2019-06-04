package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;

public interface EvaluacionExpandidaDAO extends EasyDAO<EvaluacionExpandida> {

    List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup);

    List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup, EstadoEnum estadoEnum);

    void deleteByEvaluacionParent(Long idEvaluacionParent);

    List<EvaluacionExpandida> allByEvaluacionSeccion(EvaluacionSeccion evalSecc);

    List<EvaluacionExpandida> allByGpoSeccionPlan(GrupoSeccion gpoSeccion, PlanCalificacion plan);

    //List<EvaluacionExpandida> allByGpoSeccion(GrupoSeccion grupoSeccion);
    void deleteAllByCiclo(CicloAcademico ciclo);
}
