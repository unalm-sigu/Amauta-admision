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
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
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
                .join("secciones s", "anexoBoletin ab", "curso cur")
                .leftJoin("ab.anexoSuperior asup", "planCalificacion pc", "pc.sistemaNotas")
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
    public List<GrupoSeccion> allByFilter(CicloAcademico ciclo, DepartamentoAcademico dpto, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu", "cu.departamentoAcademico da")
                .leftJoin("planCalificacion pc", "cu.planCalificacion pcc")
                .filter("ca.id", ciclo)
                .filter("da.id", dpto)
                .filter("gs.estado", EstadoEnum.ACT)
                .searchFields("gs.codigo", "cu.nombre")
                .orderBy("cu.nombre");
        return sql.all(getCurrentSession());
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
                .searchSubqueryFields("doc.codigo", "se.codigo", "gh.codigo", "au.codigo")
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
            if (!key.equals("superior.id")) {
                continue;
            }
            String value = (String) queries.get(key);
            GrupoAnexoEnum gpoE = GrupoAnexoEnum.get2(value);
            if (gpoE != null) {
                sql.filter("abs.id", gpoE.getValue());
            }
        }

        for (String key : queries.keySet()) {
            if (!key.equals("anexo.id")) {
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

}
