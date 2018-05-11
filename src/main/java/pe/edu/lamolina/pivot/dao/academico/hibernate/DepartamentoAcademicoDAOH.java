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
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.controller.academico.departamento.DepartamentoCursoDocente;

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
    public List<DepartamentoAcademico> allActiveByDyna(DynatableFilter filter, List<DepartamentoAcademico> dptos, CicloAcademico ciclo) {
        Octavia subquery = Octavia.query()
                .from(Seccion.class, "se")
                .join("se.grupoSeccion ggss")
                .filter("se.estado", EstadoEnum.ACT);

        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("da")
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .filter("ca.id", ciclo)
                .in("da.id", dptos)
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
        DynatableSql sql = new DynatableSql(filter)
                .from(DepartamentoAcademico.class, "da")
                .join("facultad fa")
                .searchFields("da.nombre", "da.codigo", "da.estado", "da.nombreLargo", "fa.nombre")
                .orderBy("da.estado", "da.id desc");
        return all(sql);
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
        sql.append("  from ").append(Curso.class.getName()).append(" as cu ");
        sql.append("  where cu.departamentoAcademico.id = da.id and cu.estado =:ACTIVO_CURSO ");
        sql.append("  ), ");
        sql.append("  ( ");
        sql.append("  select count(cu) ");
        sql.append("  from ").append(Curso.class.getName()).append(" as cu ");
        sql.append("  where cu.departamentoAcademico.id = da.id and cu.estado =:INACTIVO_CURSO ");
        sql.append("  ), ");
        sql.append("  ( ");
        sql.append("  select count(do) ");
        sql.append("  from ").append(Docente.class.getName()).append(" as do ");
        sql.append("  inner join do.persona ");
        sql.append("  where do.departamentoAcademico.id = da.id and do.estado =:ACTIVO_DOCENTE ");
        sql.append("  ), ");
        sql.append("  ( ");
        sql.append("  select count(do) ");
        sql.append("  from ").append(Docente.class.getName()).append(" as do ");
        sql.append("  inner join do.persona ");
        sql.append("  where do.departamentoAcademico.id = da.id and do.estado =:INACTIVO_DOCENTE ");
        sql.append("  ) ");
        sql.append("  ) ");
        sql.append("  from ").append(DepartamentoAcademico.class.getName()).append(" as da ");
        sql.append("  where da.id in :DEPARTAMENTOS ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("DEPARTAMENTOS", departamentosList);
        query.setString("ACTIVO_CURSO", EstadoEnum.ACT.name());
        query.setString("INACTIVO_CURSO", EstadoEnum.INA.name());
        query.setString("ACTIVO_DOCENTE", EstadoEnum.ACT.name());
        query.setString("INACTIVO_DOCENTE", EstadoEnum.INA.name());
        return query.list();
    }

    @Override
    public List<DepartamentoAcademico> allDepartemento(String nombre, Compania compania) {
        Octavia sql = Octavia.query()
                .from(DepartamentoAcademico.class, "da")
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
