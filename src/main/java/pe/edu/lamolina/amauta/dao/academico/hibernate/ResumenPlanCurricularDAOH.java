package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.academico.ResumenPlanCurricularDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;

@Repository
public class ResumenPlanCurricularDAOH extends AbstractEasyDAO<ResumenPlanCurricular> implements ResumenPlanCurricularDAO {

    public ResumenPlanCurricularDAOH() {
        super();
        setClazz(ResumenPlanCurricular.class);
    }

    @Override
    public ResumenPlanCurricular findByTipoCursoCurrPlan(TipoCursoCurricula tipoCursoCurricula, PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(ResumenPlanCurricular.class, "rpc")
                .join("planCurricular pc", "tipoCursoCurricula tcc")
                .filter("pc.id", planCurricular)
                .filter("tcc.id", tipoCursoCurricula);

        return find(sql);
    }

    @Override
    public List<ResumenPlanCurricular> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ResumenPlanCurricular.class, "cc")
                .join("planCurricular pc", "tipoCursoCurricula tcc")
                .searchFields("tcc.nombrecurtcc.nombre")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("tcc.orden");

        return all(sql);
    }

    @Override
    public List<ResumenPlanCurricular> allByPlan(PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .from(ResumenPlanCurricular.class, "rpc")
                .join("planCurricular pc", "tipoCursoCurricula tcc")
                .filter("pc.id", plan)
                .orderBy("tcc.orden");

        return all(sql);
    }

    @Override
    public List<ResumenPlanCurricular> allByPlanes(List<PlanCurricular> planesAlumnos) {
        Octavia sql = Octavia.query()
                .from(ResumenPlanCurricular.class, "rpc")
                .join("planCurricular pc", "tipoCursoCurricula tcc")
                .in("pc.id", planesAlumnos);

        return all(sql);
    }
}
