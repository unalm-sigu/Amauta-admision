package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;

public interface EvaluacionSeccionDAO extends EasyDAO<EvaluacionSeccion> {

    EvaluacionSeccion findByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum);

    EvaluacionSeccion find(Long id);

    List<EvaluacionSeccion> allByPlan(PlanCalificacion plan);

    List<EvaluacionSeccion> allByGrupoSeccion(GrupoSeccion gpoSecc);

    void deleteAllByCiclo(CicloAcademico ciclo);

    void deleteByGrupoSeccion(GrupoSeccion gpoSeccion);

}
