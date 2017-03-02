package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class DepartamentoAcademicoDAOH extends AbstractDAO<DepartamentoAcademico> implements DepartamentoAcademicoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DepartamentoAcademicoDAOH() {
        super();
        setClazz(DepartamentoAcademico.class);
    }

    @Override
    public DepartamentoAcademico find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("da");
        sqlUtil.parents("facultad f");
        sqlUtil.filter("da.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter, CicloAcademico cicloAcademico) {
        StringBuilder strb = new StringBuilder();
        strb.append(" Select ");
        strb.append("  distinct dep ");
        strb.append(" from ");
        strb.append("   GrupoSeccion gs ");
        strb.append("    inner join  gs.curso cur ");
        strb.append("    inner join  cur.departamentoAcademico dep ");
        strb.append("    inner join  gs.cicloAcademico cic ");
        strb.append("    inner join  gs.secciones secs ");
        strb.append(" where ");
        strb.append("   cic.id=:prm_ciclo and secs.estado='ACT'");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_ciclo", cicloAcademico.getId());
        List<DepartamentoAcademico> listGrupos = query.list();
        List<Long> lstDepartamentos = new ArrayList<Long>();
        for (DepartamentoAcademico dep : listGrupos) {
            lstDepartamentos.add(dep.getId());
        }

        List<String> fieldsFiltro = Arrays.asList("da.nombre", "da.codigo");

        filter.setAlias("da");
        filter.setFields(fieldsFiltro);
        filter.setParents("facultad f");
        filter.filterFix("da.estado", EstadoEnum.ACT.name());
        if (!lstDepartamentos.isEmpty()) {
            filter.filterInFix("da.id", lstDepartamentos);
        }
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
                .orderBy("da.nombre");

        return this.all(sqlUtil);
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
        strb.append(" inner join  aca_seccion sec on sec.id_grupo_seccion=gs.id ");
        strb.append(" left join  aca_plan_calificacion pc on gs.id_plan_calificacion=pc.id ");
        strb.append(" inner join  aca_curso cur on gs.id_curso=cur.id ");
        strb.append(" inner join  aca_ciclo_academico ca on gs.id_ciclo=ca.id ");
        strb.append(" inner join  aca_departamento_academico da on cur.id_departamento_academico=da.id ");
        strb.append(" where 1=1 and sec.estado='ACT'  ");
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
}
