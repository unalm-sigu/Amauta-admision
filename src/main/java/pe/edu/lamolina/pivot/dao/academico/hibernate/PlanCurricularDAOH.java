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
    public PlanCurricular find(long id) {
        Octavia sql = Octavia.query()
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .filter("pc.id", id);
        return (PlanCurricular) sql.find(getCurrentSession());
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "car.facultad fac", "car.modalidadEstudio me")
                .left("orientacionCarrera ocar", "cicloInicioVigencia cic")
                .in("car.id", carreras)
                .searchFields("car.nombre")
                .orderBy("pc.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public void updatePlanCurricular(PlanCurricular planCurricular) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update ").append(PlanCurricular.class.getSimpleName()).append(" as pc ");
        sql.append("    set fechaAprobado = :FECHA_APROBADO,  ");
        sql.append("        orientacionCarrera.id = :ORIENTACION  ");
        sql.append("  where id = :PLAN_CURRICULAR ");
        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("FECHA_APROBADO", planCurricular.getFechaAprobado());
        query.setParameter("PLAN_CURRICULAR", planCurricular.getId());

        if (planCurricular.getOrientacionCarrera() == null) {
            query.setParameter("ORIENTACION", null);
        } else {
            query.setParameter("ORIENTACION", planCurricular.getOrientacionCarrera().getId());
        }

        query.executeUpdate();
    }

}
