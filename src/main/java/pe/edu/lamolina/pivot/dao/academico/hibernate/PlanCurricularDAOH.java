package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.model.academico.Facultad;

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
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, Facultad facultad) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCurricular.class, "pc")
                .join("carrera car", "cicloInicioVigencia cic")
                .left("orientacionCarrera ocar")
                .searchFields("car.nombre")
                .orderBy("pc.id desc");
        return sql.all(getCurrentSession());
    }
}
