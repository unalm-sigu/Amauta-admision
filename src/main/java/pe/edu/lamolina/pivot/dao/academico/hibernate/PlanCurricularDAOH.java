package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.Carrera;

@Repository
public class PlanCurricularDAOH extends AbstractDAO<PlanCurricular> implements PlanCurricularDAO {

    public PlanCurricularDAOH() {
        super();
        setClazz(PlanCurricular.class);
    }

    @Override
    public PlanCurricular find(Long id) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "cicloInicioVigencia cic")
                .left("orientacionCarrera ocar")
                .filter("pc.id", id);
        return (PlanCurricular) sql.find(getCurrentSession());
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "cicloInicioVigencia cic")
                .left("orientacionCarrera ocar")
                .in("car.id", carreras)
                .searchFields("car.nombre")
                .orderBy("pc.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public void updatePlanCurricular(PlanCurricular planCurricular) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update PlanCurricular set fechaAprobado=:prm_fecha_aprob where id=:prm_id ");
        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("prm_fecha_aprob", planCurricular.getFechaAprobado());
        query.setParameter("prm_id", planCurricular.getId());
        query.executeUpdate();
    }

}
