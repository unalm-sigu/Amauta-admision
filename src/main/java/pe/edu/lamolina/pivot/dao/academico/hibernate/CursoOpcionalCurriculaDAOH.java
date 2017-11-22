package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;

@Repository
public class CursoOpcionalCurriculaDAOH extends AbstractDAO<CursoOpcionalCurricula> implements CursoOpcionalCurriculaDAO {

    public CursoOpcionalCurriculaDAOH() {
        super();
        setClazz(CursoOpcionalCurricula.class);
    }

    @Override
    public List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc")
                .searchFields("cur.nombre", "cur.codigo")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public Map countByPlanesCurricular(List<PlanCurricular> planesCurricular) {
        List<Long> ids = new ArrayList<>();
        for (PlanCurricular planCurricular : planesCurricular) {
            ids.add(planCurricular.getId());
        }
        StringBuilder sql = new StringBuilder();
        sql.append("Select pc.id, count(cc) from CursoOpcionalCurricula cc inner join cc.planCurricular pc where pc.id in (:prm_planes) group by pc.id");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("prm_planes", ids);
        List<Object[]> resultado = query.list();
        Map result = new HashMap();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

}
