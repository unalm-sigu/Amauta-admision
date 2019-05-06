package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

@Repository
public class CursoCurriculaDAOH extends AbstractEasyDAO<CursoCurricula> implements CursoCurriculaDAO {

    public CursoCurriculaDAOH() {
        super();
        setClazz(CursoCurricula.class);
    }

    @Override
    public CursoCurricula find(long id) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc")
                .leftJoin("cur.departamentoAcademico da", "da.facultad fac")
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
    public Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas) {
        Octavia sql = Octavia.query()
                .select("pc.id", "count(cc)")
                .from(CursoCurricula.class, "cc")
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

    @Override
    public List<CursoCurricula> allByPlanCurricular(PlanCurricular plan) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc", "curso cu")
                .left("cu.departamentoAcademico")
                .filter("pc.id", plan)
                .orderBy("cc.numeroCiclo", "cc.numeroCurso");
        return all(sql);
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
        sql.orderBy("tcc.orden", "cur.nombre");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByPlanCurricularNroCiclo(PlanCurricular plan, Integer nro) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc", "curso cu")
                .left("cu.departamentoAcademico")
                .filter("pc.id", plan)
                .filter("cc.numeroCiclo", nro);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByNombrePlanNroCiclo(CursoCurricula cursoCurricula, Integer limit) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc", "cur.departamentoAcademico da", "da.facultad fac")
                .leftJoin("cur.carrera ca")
                .beginBlock()
                .__().like("cur.nombre", cursoCurricula.getCurso().getNombre())
                .__().like("cur.codigo", cursoCurricula.getCurso().getNombre())
                .endBlock()
                .filter("pc.id", cursoCurricula.getPlanCurricular())
                .filter("cc.numeroCiclo", "<=", cursoCurricula.getNumeroCiclo())
                .filter("cc.numeroCiclo", ">", 0)
                .orderBy("cur.nombre")
                .limit(limit);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<CursoCurricula> allByTipoCursoCurriculaEnum(TipoCursoCurriculaEnum tipoCursoCurriculaEnum) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc", "curso cu")
                .left("cu.departamentoAcademico")
                .filter("tcc.codigo", tipoCursoCurriculaEnum);
        return all(sql);
    }

    @Override
    public List<CursoCurricula> allByPlanes(List<PlanCurricular> planes) {
        Octavia sql = Octavia.query()
                .from(CursoCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "planCurricular pc", "curso cu")
                .left("cu.departamentoAcademico")
                .in("pc.id", planes)
                .orderBy("cc.numeroCiclo", "cc.numeroCurso");
        return all(sql);
    }

}
