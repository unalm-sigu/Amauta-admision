package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;

@Repository
public class CursoCurriculaDAOH extends AbstractDAO<CursoCurricula> implements CursoCurriculaDAO {

    public CursoCurriculaDAOH() {
        super();
        setClazz(CursoCurricula.class);
    }

    @Override
    public CursoCurricula find(Long id) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc", "cur.departamentoAcademico da", "da.facultad fac")
                .filter("cc.id", id);
        return (CursoCurricula) sql.find(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByFilter(TipoCursoCurricula tipoCursoCurricula) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc")
                .filter("ca.id", tipoCursoCurricula.getId());
        return sql.all(getCurrentSession());
    }

    @Override
    public Map countByPlanesCurricular(List<PlanCurricular> planesCurricular) {
        List<Long> ids = new ArrayList<>();
        for (PlanCurricular planCurricular : planesCurricular) {
            ids.add(planCurricular.getId());
        }
        StringBuilder sql = new StringBuilder();
        sql.append("Select pc.id, count(cc) from CursoCurricula cc inner join cc.planCurricular pc where pc.id in (:prm_planes) group by pc.id");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("prm_planes", ids);
        List<Object[]> resultado = query.list();
        Map result = new HashMap();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public List<CursoCurricula> allByPlan(PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc")
                .filter("pc.id", planCurricular.getId());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc")
                .searchFields("tcc.nombrecurtcc.nombre")
                .filter("pc.id", filter.getQueries().get("planc"));
        if (filter.getQueries().get("numCic") != null) {
            sql.filter("cc.numeroCiclo", filter.getQueries().get("numCic"));
        }
        sql.orderBy("cur.nombre desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByNombreFilter(Long planCurriculaId, Integer numeroCiclo, String nombre, Integer limit) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc", "cur.departamentoAcademico da", "da.facultad fac")
                .like("cur.nombre", nombre)
                .filter("pc.id", planCurriculaId)
                .filter("cc.numeroCiclo", "<", numeroCiclo)
                .orderBy("cur.nombre")
                .limit(limit);
        return sql.all(getCurrentSession());
    }

    @Override
    public void updateCreditoRequisito(CursoCurricula cursoCurricula) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update CursoCurricula set creditosRequisito=:prm_creditos_req where id=:prm_id ");
        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("prm_creditos_req", cursoCurricula.getCreditosRequisito());
        query.setParameter("prm_id", cursoCurricula.getId());
        query.executeUpdate();
    }

}
