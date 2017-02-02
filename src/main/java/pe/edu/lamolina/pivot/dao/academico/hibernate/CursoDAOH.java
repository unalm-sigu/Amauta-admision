package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

@Repository
public class CursoDAOH extends AbstractDAO<Curso> implements CursoDAO {

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public Curso find(Long idCurso) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("left planCalificacion pc", "left departamentoAcademico da", "left _da.facultad")
                .filter("cur.id", idCurso);
        return find(sqlUtil);
    }

    @Override
    public List<Curso> allForSistemaCalificacion(String nombre, Long idDepartamentoAca, Long planCalificacion, Long idCiclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" left join fetch cur.departamentoAcademico da ");
        sql.append(" left join fetch cur.planCalificacion pc ");
        sql.append(" where 1=1 ");
        sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        sql.append("  and    cur.id in ( select cu.id ");
        sql.append("                       from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("                      inner join gs.curso cu ");
        sql.append("                      inner join gs.cicloAcademico ca ");
        sql.append("                      where ca.id = :CICLO ) ");
        sql.append("  and    da.id = :DEP_ACA ");
        sql.append("  and    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("PLAN_CAL", planCalificacion);
        query.setParameter("DEP_ACA", idDepartamentoAca);
        query.setParameter("CICLO", idCiclo);
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, Long idPlanCalificacion, Long idDepartamentoAcademico) {
        List<String> fieldsFiltro = Arrays.asList(
                "c.nombre", "c.codigo", "c.fechaPlanCalificacion");

        filter.setFields(fieldsFiltro);

        filter.setAlias("c");
        filter.setParents("planCalificacion pc", "departamentoAcademico da");

        filter.filterFix("pc.id", idPlanCalificacion);
        filter.filterFix("da.id", idDepartamentoAcademico);

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filterFixIn = filter.getFiltersInFixed();
        if (filterFixIn != null) {
            for (Object key : filterFixIn.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
            }
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

    @Override
    public List<Curso> allByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("planCalificacion pc")
                .filter("pc.id", plan);
        return all(sqlUtil);
    }

}
