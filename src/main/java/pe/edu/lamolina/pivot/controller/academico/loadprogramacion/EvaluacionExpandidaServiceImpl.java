package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EvaluacionExpandidaServiceImpl implements EvaluacionExpandidaService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;

    @Override
    @Transactional
    public void recalcularNivel(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        List<GrupoSeccion> gposSeccion = grupoSeccionDAO.allByCiclo(cicloAcademico);
        Map<Long, PlanCalificacion> mapPlanes = MapUtil.storeItems("planCalificacion.id", "planCalificacion", gposSeccion);
        List<PlanCalificacion> planes = new ArrayList(mapPlanes.values());
        List<EvaluacionPlan> evalucionesPlanes = evaluacionPlanDAO.allByPlanes(planes);
        Map<Long, List<EvaluacionPlan>> mapEvaluacionPlan = MapUtil.storeLists("planCalificacion.id", evalucionesPlanes);
        for (PlanCalificacion plan : planes) {
            List<EvaluacionPlan> evaluacionesPlan = mapEvaluacionPlan.get(plan.getId());
            plan.setEvaluacionPlan(evaluacionesPlan);
        }

    }

}
