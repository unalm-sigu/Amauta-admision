package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.PlanCalificacion;

@Repository
public class EvaluacionPlanDAOH extends AbstractEasyDAO<EvaluacionPlan> implements EvaluacionPlanDAO {

    public EvaluacionPlanDAOH() {
        super();
        setClazz(EvaluacionPlan.class);
    }

    @Override
    public List<EvaluacionPlan> allByDynatable(DynatableFilter filter, Long idPlanCalificacion) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EvaluacionPlan.class, "evap")
                .join("tipoEvaluacion te", "planCalificacion pc")
                .filter("pc.id", idPlanCalificacion)
                .orderBy("evap.id desc");

        return all(sql);
    }

    @Override
    public List<EvaluacionPlan> allByFilter(Long idPlanCalificacion) {
        Octavia sql = Octavia.query()
                .from(EvaluacionPlan.class, "evap")
                .join("tipoEvaluacion te", "planCalificacion pc")
                .filter("pc.id", idPlanCalificacion);

        return all(sql);
    }

    @Override
    public List<EvaluacionPlan> allByPlan(PlanCalificacion planCalificacion) {
        Octavia sql = Octavia.query()
                .from(EvaluacionPlan.class, "evap")
                .join("tipoEvaluacion te", "planCalificacion pc")
                .filter("pc.id", planCalificacion);

        return all(sql);
    }

    @Override
    public void deleleByPlan(PlanCalificacion plan) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(EvaluacionPlan.class.getName()).append(" ep ");
        sql.append("  where ep.planCalificacion.id = :PLAN ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("PLAN", plan.getId());

        query.executeUpdate();
    }

    @Override
    public List<EvaluacionPlan> allByPlanes(List<PlanCalificacion> planes) {
        Octavia sql = Octavia.query()
                .from(EvaluacionPlan.class, "evap")
                .join("tipoEvaluacion te", "planCalificacion pc")
                .in("pc.id", planes);

        return all(sql);
    }

}
