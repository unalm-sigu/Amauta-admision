package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

@Repository
public class GrupoSeccionDAOH extends AbstractDAO<GrupoSeccion> implements GrupoSeccionDAO {

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "planCalificacion pc", "curso cur")
                .filter("gp.id", idGrupoSeccion);
        return find(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("left planCalificacion pc", "curso cur", "cicloAcademico ca", "left _cur.planCalificacion pcc")
                .parents("_cur.departamentoAcademico da")
                .filter("ca.id", cicloAcademico);
        if (departamentoAcademico != null) {
            sqlUtil.filter("da.id", departamentoAcademico.getId());
        }
        if (ids != null) {
            sqlUtil.filterIn("gp.id", ids);
        }
        return all(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter filter) {

        List<String> fieldsFiltro = Arrays.asList("gp.codigo", "cur.nombre");
        filter.setAlias("gp");
        filter.setFields(fieldsFiltro);
        filter.setParents("left planCalificacion pc", "curso cur", "cicloAcademico ca", "left _cur.planCalificacion pcc", "_cur.departamentoAcademico da");

        filter.filterFix("ca.id", cicloAcademico.getId());
        filter.filterFix("da.id", departamentoAcademico.getId());

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }

        Map filtersInFix = filter.getFiltersInFixed();
        for (Object key : filtersInFix.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filtersInFix.get(key));
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("planCalificacion pc")
                .filter("pc.id", plan);

        return all(sqlUtil);
    }

    @Override
    public GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "left planCalificacion pc", "curso cur", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("gp.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<GrupoSeccion> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "left planCalificacion pc", "curso cur", "cicloAcademico ca")
                .filter("ca.id", ciclo);
        return all(sqlUtil);
    }

}
