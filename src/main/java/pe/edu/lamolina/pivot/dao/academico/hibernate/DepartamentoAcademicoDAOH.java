package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.controller.academico.departamento.DepartamentoCursoDocente;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class DepartamentoAcademicoDAOH extends AbstractEasyDAO<DepartamentoAcademico> implements DepartamentoAcademicoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DepartamentoAcademicoDAOH() {
        super();
        setClazz(DepartamentoAcademico.class);
    }

    @Override
    public DepartamentoAcademico find(Long id) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "da")
                .join("facultad fa")
                .filter("da.id", id);

        return find(sql);
    }

    @Override
    public List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter, CicloAcademico ciclo) {
        Octavia subquery = Octavia.query()
                .from(Seccion.class, "se")
                .join("se.grupoSeccion ggss")
                .filter("se.estado", EstadoEnum.ACT);

        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("da")
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .filter("ca.id", ciclo)
                .filter("da.estado", EstadoEnum.ACT)
                .exists(subquery)
                .linkedBy("gs.id", "ggss.id")
                .searchFields("da.nombre", "da.codigo")
                .orderBy("da.nombre");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<DepartamentoAcademico> countByFilter(List<Long> ids, CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico) {
        StringBuilder strb = new StringBuilder();
        strb.append(" Select ");
        //     strb.append(" new pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico(");
        strb.append("           da.id as id, ");
        strb.append("           sum(if(estado_grupo='CER',1,0)) as cantidadGruposCerrados, ");
        strb.append("           sum(if(estado_grupo='ABI' or estado_grupo='RAB',1,0)) as cantidadGruposAbiertos, ");
        strb.append("           count(*) as totalGrupos ");
        //   strb.append(" ) ");
        strb.append(" from ");
        strb.append(" aca_grupo_seccion gs ");
        //      strb.append(" inner join  aca_seccion sec on sec.id_grupo_seccion=gs.id ");
        strb.append(" left join  aca_plan_calificacion pc on gs.id_plan_calificacion=pc.id ");
        strb.append(" inner join  aca_curso cur on gs.id_curso=cur.id ");
        strb.append(" inner join  aca_ciclo_academico ca on gs.id_ciclo=ca.id ");
        strb.append(" inner join  aca_departamento_academico da on cur.id_departamento_academico=da.id ");
        strb.append(" where 1=1 and gs.estado='ACT'  ");
        strb.append(" and da.id in (:prm_departamentos) ");
        if (departamentoAcademico != null) {
            strb.append(" and ds.id=:prm_departamento ");
        }
        strb.append(" and ca.id=:prm_ciclo ");
        strb.append(" group by da.id ");
        SQLQuery query = getCurrentSession().createSQLQuery(strb.toString());
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        if (departamentoAcademico != null) {
            query.setParameter("prm_departamento", departamentoAcademico.getId());
        }
        query.setParameter("prm_ciclo", cicloAcademico.getId());
        query.setParameterList("prm_departamentos", ids);

        List<DepartamentoAcademico> result = new ArrayList<>();
        List<Map> lstData = query.list();

        for (Map map : lstData) {
            result.add(new DepartamentoAcademico(map.get("id"), map.get("cantidadGruposCerrados"), map.get("cantidadGruposAbiertos"), map.get("totalGrupos")));
        }
        return result;
    }

    @Override
    public List<DepartamentoAcademico> allByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "de")
                .join("facultad fa", "fa.compania co")
                .filter("co.id", compania);

        return all(sql);
    }

    @Override
    public List<DepartamentoAcademico> allDynatable(DynatableFilter filter) {

        StringBuilder sql;
        Query query;

        String search = filter.getSearchValue();

        if (!StringUtils.isEmpty(search)) {
            search = "%" + search.replaceAll(" ", "%") + "%";
        }

        {
            sql = new StringBuilder();
            sql.append("  select count( distinct da ) ");
            sql.append("  from ").append(DepartamentoAcademico.class.getName()).append(" as da ");
            sql.append("  inner join  da.facultad fa ");
            sql.append("  where 1 = 1 ");

            query = getCurrentSession().createQuery(sql.toString());
            filter.setTotal(((Long) query.uniqueResult()).intValue());
        }

        {
            sql = new StringBuilder();
            sql.append("  select count( distinct da ) ");
            sql.append("  from ").append(DepartamentoAcademico.class.getName()).append(" as da ");
            sql.append("  inner join  da.facultad fa ");
            sql.append("  where 1 = 1 ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    da.nombre like :SEARCH ");
                sql.append("    or da.codigo like :SEARCH ");
                sql.append("    or da.estado like :SEARCH ");
                sql.append("    or da.nombreLargo like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());
            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }
            filter.setFiltered(((Long) query.uniqueResult()).intValue());
        }

        {
            sql = new StringBuilder();
            sql.append("  select distinct da ");
            sql.append("  from ").append(DepartamentoAcademico.class.getName()).append(" as da ");
            sql.append("  inner join  da.facultad fa ");
            sql.append("  where 1 = 1 ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    da.nombre like :SEARCH ");
                sql.append("    or da.codigo like :SEARCH ");
                sql.append("    or da.estado like :SEARCH ");
                sql.append("    or da.nombreLargo like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());
            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }
            query.setFirstResult((filter.getPage() - 1) * filter.getPerPage());
            query.setMaxResults(filter.getPerPage());

            return query.list();
        }
    }

    @Override
    public DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico) {

        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "da")
                .filter("da.id", idDepartamentoAcademico);

        return (DepartamentoAcademico) sql.find(getCurrentSession());
    }

    @Override
    public List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<Long> departamentosList) {

        StringBuilder sql = new StringBuilder();
        sql.append("  select new ").append(DepartamentoCursoDocente.class.getName()).append(" ( ");
        sql.append("  da.id, ");
        sql.append("  ( ");
        sql.append("  select count(cu) ");
        sql.append("  from ").append(Curso.class.getSimpleName()).append(" as cu ");
        sql.append("  where cu.departamentoAcademico.id = da.id ");
        sql.append("  ), ");
        sql.append("  ( ");
        sql.append("  select count(do) ");
        sql.append("  from ").append(Docente.class.getSimpleName()).append(" as do ");
        sql.append("  where do.departamentoAcademico.id = da.id ");
        sql.append("  ) ");
        sql.append("  ) ");
        sql.append("  from ").append(DepartamentoAcademico.class.getName()).append(" as da ");
        sql.append("  where da.id in :DEPARTAMENTOS ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("DEPARTAMENTOS", departamentosList);
        return query.list();
    }

    @Override
    public List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "de")
                .join("facultad fa", "fa.compania co")
                .beginBlock()
                .__().like("da.codigo", nombre)
                .__().like("da.nombre", nombre)
                .endBlock()
                .filter("co.id", compania)
                .orderBy("da.nombre")
                .limit(10);

        return all(sql);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentos(String nombre) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "da")
                .join("facultad fa")
                .filter("da.nombre", "like", nombre);

        return all(sql);
    }
}
