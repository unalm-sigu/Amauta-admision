package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;

@Repository
public class PlanCalificacionDAOH extends AbstractDAO<PlanCalificacion> implements PlanCalificacionDAO {

    public PlanCalificacionDAOH() {
        super();
        setClazz(PlanCalificacion.class);
    }

    @Override
    public List<PlanCalificacion> allByDynatable(DynatableFilter filter) {
        filter.setAlias("pc");

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }
        Map filterFixIn = filter.getFiltersInFixed();
        for (Object key : filterFixIn.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
        }

        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }
}
