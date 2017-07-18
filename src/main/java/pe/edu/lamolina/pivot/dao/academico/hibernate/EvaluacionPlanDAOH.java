package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

@Repository
public class EvaluacionPlanDAOH extends AbstractDAO<EvaluacionPlan> implements EvaluacionPlanDAO {

    public EvaluacionPlanDAOH() {
        super();
        setClazz(EvaluacionPlan.class);
    }

    @Override
    public List<EvaluacionPlan> allByDynatable(DynatableFilter filter, Long idPlanCalificacion) {
        filter.setAlias("evap");
        filter.setParents("tipoEvaluacion te", "planCalificacion pc");

        filter.filterFix("pc.id", idPlanCalificacion);

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filterFixIn = filter.getFiltersInFixed();
        if (filterFixIn != null) {
            for (Object key : filterFixIn.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
            }
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

    @Override
    public List<EvaluacionPlan> allByFilter(Long idPlanCalificacion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("evap");
        sqlUtil.parents("tipoEvaluacion te", "planCalificacion pc");
        sqlUtil.filter("pc.id", idPlanCalificacion);
        return this.all(sqlUtil);
    }

    @Override
    public List<EvaluacionPlan> allByPlan(PlanCalificacion planCalificacion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("evap");
        sqlUtil.parents("tipoEvaluacion te", "planCalificacion pc");
        sqlUtil.filter("pc.id", planCalificacion);
        return this.all(sqlUtil);
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
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("evap")
                .parents("planCalificacion pc", "tipoEvaluacion")
                .filterIn("pc.id", planes);
        return this.all(sqlUtil);
    }

}
