package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;

@Repository
public class CursoAdicionalCurriculaDAOH extends AbstractDAO<CursoAdicionalCurricula> implements CursoAdicionalCurriculaDAO {

    public CursoAdicionalCurriculaDAOH() {
        super();
        setClazz(CursoAdicionalCurricula.class);
    }

    @Override
    public List<CursoAdicionalCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoAdicionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc")
                .searchFields("cur.nombre")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoAdicionalCurricula> allByPlanCurricular(PlanCurricular curricula) {
        Octavia sql = Octavia.query()
                .from(CursoAdicionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc")
                .filter("pc.id", curricula);
        return sql.all(getCurrentSession());
    }

    @Override
    public Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas) {
        Octavia sql = Octavia.query()
                .select("pc.id", "count(cc)")
                .from(CursoAdicionalCurricula.class, "cc")
                .join("planCurricular pc")
                .in("pc.id", curriculas)
                .groupBy("pc.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }
}
