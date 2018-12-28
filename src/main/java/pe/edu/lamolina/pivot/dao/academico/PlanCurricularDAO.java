package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface PlanCurricularDAO extends EasyDAO<PlanCurricular> {

    List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras);

    void updatePlanCurricular(PlanCurricular planCurricular);

    List<PlanCurricular> allActivoByCarrera(Carrera carrera);

    List<PlanCurricular> allActivoByOrientacion(Carrera carrera, OrientacionCarrera orientacion);

    List<PlanCurricular> allActivosByCarrera(Carrera carrera);

}
