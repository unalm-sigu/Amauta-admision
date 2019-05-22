package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;

@Repository
public class CursoOpcionalCurriculaDAOH extends AbstractEasyDAO<CursoOpcionalCurricula> implements CursoOpcionalCurriculaDAO {

    public CursoOpcionalCurriculaDAOH() {
        super();
        setClazz(CursoOpcionalCurricula.class);
    }

    @Override
    public CursoOpcionalCurricula find(long id) {
        Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .filter("cc.id", id);

        return find(sql);
    }

    @Override
    public List<CursoOpcionalCurricula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .searchFields("cur.nombre", "cur.codigo", "cur.codigoAnterior1")
                .filter("pc.id", filter.getQueries().get("planc"))
                .orderBy("tcc.orden desc", "cur.nombre");
        return all(sql);
    }

    @Override
    public Map<Long, Integer> countByPlanesCurricular(List<PlanCurricular> curriculas) {
        Octavia sql = Octavia.query()
                .select("pc.id", "count(cc)")
                .from(CursoOpcionalCurricula.class, "cc")
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
    public List<CursoOpcionalCurricula> allByPlanCurricular(PlanCurricular planCurricular) {
        Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .filter("pc.id", planCurricular);

        return all(sql);
    }

    @Override
    public List<CursoOpcionalCurricula> allByNombrePlan(CursoCurricula cursoCurricula, Integer limit) {
        Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .join("tipoCursoCurricula tcc", "curso cur", "planCurricular pc", "cur.departamentoAcademico da", "da.facultad fac")
                .leftJoin("cur.carrera ca")
                .beginBlock()
                .__().like("cur.nombre", cursoCurricula.getCurso().getNombre())
                .__().like("cur.codigo", cursoCurricula.getCurso().getNombre())
                .endBlock()
                .filter("pc.id", cursoCurricula.getPlanCurricular())
                .orderBy("cur.nombre")
                .limit(limit);
        return sql.all(getCurrentSession());
    }

    @Override
    public CursoOpcionalCurricula findByPlanCurricularAndCurso(PlanCurricular planCurricular, Curso curso) {
        Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .filter("pc.id", planCurricular)
                .filter("cur.id", curso);

        return find(sql);
    }

    @Override
    public List<CursoOpcionalCurricula> allByPlanCurricular(List<PlanCurricular> planCurricular) {
        Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .left("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .in("pc.id", planCurricular);

        return all(sql);
    }

    @Override
    public List<CursoOpcionalCurricula> allNotPlanCurricularAndCurso(List<PlanCurricular> planesCurricular) {
         Octavia sql = Octavia.query()
                .from(CursoOpcionalCurricula.class, "cc")
                .join("curso cur", "planCurricular pc", "tipoCursoCurricula tcc")
                .notIn("pc.id", planesCurricular);

        return all(sql);
    }

}
