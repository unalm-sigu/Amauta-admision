package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

@Repository
public class GrupoSeccionDAOH extends AbstractDAO<GrupoSeccion> implements GrupoSeccionDAO {

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("secciones s", "left planCalificacion pc", "left _pc.sistemaNotas", "curso cur")
                .filter("gp.id", idGrupoSeccion);
        return find(sqlUtil);
    }

    @Override
    public GrupoSeccion findLast() {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .orderBy("id desc")
                .limit(1);
        return (GrupoSeccion) sql.find(getCurrentSession());
    }

    @Override
    public List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, EstadoEnum estadoEnum) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gp")
                .parents("left planCalificacion pc", "curso cur", "cicloAcademico ca", "left _cur.planCalificacion pcc", "left _cur.planCalificacionRegular pcr")
                .parents("_cur.departamentoAcademico da");
        if (cicloAcademico != null) {
            sqlUtil.filter("ca.id", cicloAcademico);
        }
        if (departamentoAcademico != null) {
            sqlUtil.filter("da.id", departamentoAcademico.getId());
        }
        if (ids != null) {
            sqlUtil.filterIn("gp.id", ids);
        }
        if (estadoEnum != null) {
            sqlUtil.filter("gp.estado", estadoEnum.name());
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
                .setPageSize(filter.getPerPage())
                .orderBy("cur.nombre");

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

    @Override
    public List<DocenteCursoPlan> allDocenteCursoPlanByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("doc.id", "cu.id", "count(distinct pc.id)", "max(pc.id)")
                .into(DocenteCursoPlan.class)
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "doc.persona per", "seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso cu")
                .join("gs.planCalificacion pc")
                .filter("ca.id", ciclo)
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("s.tipoSeccion", "<>", TipoSeccionEnum.PCUR)
                .filter("ds.principal", 1)
                .groupBy("doc.id", "cu.id");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoSeccion> allByDynatable(pe.albatross.octavia.dynatable.DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "anexoBoletin ab", "curso cu", "planCalificacion pc")
                .leftJoin("ab.anexoSuperior ass")
                .searchFields("cu.nombre")
                .orderBy("gs.id desc");
        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);
        return sql.all(getCurrentSession());
    }

    private void setGrupoAnexo(pe.albatross.octavia.dynatable.DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("ass.id")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("ingresantes")) {
                sql.filter("ass.id", GrupoAnexoEnum.INGRESANTE.getValue());

            } else if (values.equals("departamentos")) {
                sql.filter("ass.id", GrupoAnexoEnum.DPTO.getValue());

            } else if (values.equals("postGrados")) {
                sql.filter("ass.id", GrupoAnexoEnum.POSTGRADO.getValue());

            } else if (values.equals("actividades")) {
                sql.filter("ass.id", GrupoAnexoEnum.ACTIVIDADES.getValue());
            }
        }
    }

    @Override
    public GpoSeccionResumen resumen() {
        StringBuilder sql = new StringBuilder();
        sql.append("select new ").append(GpoSeccionResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case abs.id when :INGRE then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :DPTO  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :POST  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :ACTI  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(AnexoBoletin.class.getName()).append(" as ab ");
        sql.append(" inner join  ab.anexoSuperior abs ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("INGRE", GrupoAnexoEnum.INGRESANTE.getValue());
        query.setString("DPTO", GrupoAnexoEnum.DPTO.getValue());
        query.setString("ACTI", GrupoAnexoEnum.ACTIVIDADES.getValue());
        query.setString("POST", GrupoAnexoEnum.POSTGRADO.getValue());

        return (GpoSeccionResumen) query.uniqueResult();
    }

}
