package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.ABI;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.acta.ActaResumen;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;

@Repository
public class GrupoSeccionDAOH extends AbstractEasyDAO<GrupoSeccion> implements GrupoSeccionDAO {

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(Long idGrupoSeccion) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("anexoBoletin ab", "curso cur")
                .leftJoin("ab.anexoSuperior asup", "planCalificacion pc", "pc.sistemaNotas", "secciones s", "cicloAcademico ca")
                .filter("gs.id", idGrupoSeccion);
        return find(sql);
    }

    @Override
    public GrupoSeccion findLast() {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .orderBy("id desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public String findMaxCodigoByCiclo(CicloAcademico cicloAcademico) {
        /*  Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .orderBy("gs.id desc")
                .limit(1);
             return find(sql);*/

        StringBuilder strb = new StringBuilder();
        strb.append("Select max(gs.codigo) from GrupoSeccion gs ");
        strb.append(" join gs.cicloAcademico cs ");
        strb.append(" where cs.id=:prm_ciclo ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_ciclo", cicloAcademico.getId());

        return (String) query.uniqueResult();
    }

    @Override
    public List<GrupoSeccion> allUnusedByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("gs.codigo", "like", "Y%")
                .orderBy("gs.codigo");
        return all(sql);
    }

    @Override
    public List<String> allCodigoByCiclo(CicloAcademico cicloAcademico) {
        /*  Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .orderBy("gs.id desc")
                .limit(1);
             return find(sql);*/

        StringBuilder strb = new StringBuilder();
        strb.append("Select gs.codigo from GrupoSeccion gs ");
        strb.append(" join gs.cicloAcademico cs ");
        strb.append(" where cs.id=:prm_ciclo ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_ciclo", cicloAcademico.getId());

        return query.list();
    }

    @Override
    public List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico ciclo, DepartamentoAcademico dpto, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca", "cur.departamentoAcademico da")
                .leftJoin("cur.planCalificacion pcc", "cur.planCalificacionRegular pcr");

        if (ciclo != null) {
            sql.filter("ca.id", ciclo);
        }
        if (dpto != null) {
            sql.filter("da.id", dpto);
        }
        if (ids != null) {
            sql.in("gs.id", ids);
        }
        if (estadoEnum != null) {
            sql.filter("gs.estado", estadoEnum);
        }

        return all(sql);
    }

    @Override
    public List<GrupoSeccion> allByDynatableCicloDpto(CicloAcademico ciclo, DepartamentoAcademico dpto, DynatableFilter filter) {
        Octavia sqlSub = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gssub", "docente doc")
                .join("doc.persona per")
                .filter("ds.principal", 1)
                .filter("sec.tipoSeccion", "<>", TipoSeccionEnum.PCUR);

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu", "cu.departamentoAcademico da")
                .leftJoin("planCalificacion pc", "cu.planCalificacion pcc")
                .filter("ca.id", ciclo)
                .filter("da.id", dpto)
                .filter("gs.estado", EstadoEnum.ACT)
                .searchFields("gs.codigo", "cu.nombre", "cu.codigo")
                .searchSubquery(sqlSub)
                .subqueryLinkedBy("gs.id", "gssub.id")
                .searchSubqueryComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchSubqueryComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("cu.nombre");
        sql.beginRelativeFilters();
        setCondicionEstado(filter, sql);
        return all(sql);
    }

    public void setCondicionEstado(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            String values = (String) queries.get(key);
            if (values.equals("ABI")) {
                sql.filter("gs.estadoGrupo", ABI);
            } else if (values.equals("CER")) {
                sql.filter("gs.estadoGrupo", CER);
            }
            if (values.equals("pregrado")) {
                sql.filter("cu.nivel", "<=", 6);
            } else if (values.equals("posgrado")) {
                sql.filter("cu.nivel", ">=", 7);
            }
        }
    }

    @Override
    public List<GrupoSeccion> allByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("planCalificacion pc")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public GrupoSeccion findByCodeCiclo(String codigo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("secciones s", "curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc")
                .filter("ca.id", ciclo)
                .filter("gs.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<GrupoSeccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("secciones s", "curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc")
                .filter("ca.id", ciclo);

        return all(sql);
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
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        Octavia subQuery = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion se", "se.grupoSeccion ggss")
                .left("doc.persona per", "se.grupoHoras gh", "se.aula au");

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "anexoBoletin ab", "curso cu")
                .leftJoin("ab.anexoSuperior abs", "planCalificacion pc")
                .filter("ca.id", ciclo)
                .searchFields("cu.nombre", "cu.codigo", "ab.nombre", "abs.nombre")
                .searchSubquery(subQuery)
                .subqueryLinkedBy("gs.id", "ggss.id")
                .searchSubqueryFields("doc.codigo", "se.codigo2", "gh.codigo", "au.codigo")
                .searchSubqueryComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchSubqueryComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("gs.id desc");

        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);

        return sql.all(getCurrentSession());
    }

    private void setGrupoAnexo(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("anexo-superior")) {
                continue;
            }
            String value = (String) queries.get(key);
            GrupoAnexoEnum gpoE = GrupoAnexoEnum.get2(value);
            if (gpoE != null) {
                sql.filter("abs.id", gpoE.getValue());
            }
        }

        for (String key : queries.keySet()) {
            if (!key.equals("anexo")) {
                continue;
            }
            sql.filter("ab.id", queries.get(key));
        }
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new ").append(GpoSeccionResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case abs.id when :INGRE then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :DPTO  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :POST  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :ACTI  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(GrupoSeccion.class.getName()).append(" as gs ");
        sql.append(" inner join gs.cicloAcademico ca ");
        sql.append(" inner join gs.anexoBoletin ab ");
        sql.append(" inner join ab.anexoSuperior abs ");
        sql.append(" where ca.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("INGRE", GrupoAnexoEnum.INGRESANTE.getValue());
        query.setParameter("DPTO", GrupoAnexoEnum.DPTO.getValue());
        query.setParameter("ACTI", GrupoAnexoEnum.ACTIVIDADES.getValue());
        query.setParameter("POST", GrupoAnexoEnum.POSTGRADO.getValue());
        query.setParameter("CICLO", ciclo.getId());

        return (GpoSeccionResumen) query.uniqueResult();
    }

    @Override
    public void updateEstadoFechaModUsuarioMod(GrupoSeccion grupoSeccion) {
        Octavia octavia = Octavia.update(GrupoSeccion.class);
        octavia.set(grupoSeccion, "estado");
        octavia.set(grupoSeccion, "usuarioModificacion");
        octavia.set(grupoSeccion, "fechaModificacion");
        this.update(grupoSeccion);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public GrupoSeccion findLock(Long id) {
        return (GrupoSeccion) getCurrentSession().load(GrupoSeccion.class, id, LockOptions.UPGRADE);
    }

    @Override
    public List<GrupoSeccion> allActivoByCiclo(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("secciones s", "curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc")
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<GrupoSeccion> allActivoByCicloGrupoNoCerrado(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("secciones s", "curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc")
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("gs.estadoGrupo", "<>", EstadoGrupoSeccionEnum.CER)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public ActaResumen findResumenByDepartamento(CicloAcademico ciclo, DepartamentoAcademico dpto) {
        Octavia sql = Octavia.query()
                .select(
                        "sum(case gs.estadoGrupo when 'ABI' then 1 else 0 end)",
                        "sum(case gs.estadoGrupo when 'CER' then 1 else 0 end)",
                        "sum(case when cu.nivel <= 6 then 1 else 0 end)",
                        "sum(case when cu.nivel >= 7 then 1 else 0 end)")
                .into(ActaResumen.class)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu", "cu.departamentoAcademico da")
                .filter("ca.id", ciclo)
                .filter("da.id", dpto)
                .filter("gs.estado", EstadoEnum.ACT)
                .orderBy("cu.nombre");

        return (ActaResumen) sql.find(getCurrentSession());
    }

    @Override
    public List<GrupoSeccion> allByCicloCurso(CicloAcademico ciclo, String codigo, Long curso) {
        Octavia sql = Octavia.query(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cur")
                .filter("ca.id", ciclo)
                .like("gs.codigo", codigo)
                .limit(15);
        if (curso != null) {
            sql.filter("cur.id", curso);
        }
        return all(sql);
    }

    @Override
    public List<GrupoSeccion> allActivosByDocenteCiclo(Docente docente, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("gs")
                .from(DocenteSeccion.class, "ds")
                .join("ds.seccion se", "se.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso cur")
                .join("ds.docente doc", "doc.persona per")
                .leftJoin("per.tipoDocumento")
                .filter("ca.id", ciclo)
                .filter("doc.id", docente)
                .filter("se.estado", EstadoEnum.ACT)
                .filter("ds.estado", EstadoEnum.ACT);

        return all(sql);
    }

}
