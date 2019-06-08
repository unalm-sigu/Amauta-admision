package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoCursoCachimboEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.ABI;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.academico.acta.ActaResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.plancalificacurso.DocenteCursoPlan;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;

@Repository
public class GrupoSeccionDAOH extends AbstractEasyDAO<GrupoSeccion> implements GrupoSeccionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GrupoSeccionDAOH() {
        super();
        setClazz(GrupoSeccion.class);
    }

    @Override
    public GrupoSeccion find(long idGrupoSeccion) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("anexoBoletin ab", "curso cur", "cicloAcademico ca")
                .leftJoin("ab.anexoSuperior asup", "planCalificacion pc", "pc.sistemaNotas", "cur.departamentoAcademico")
                .leftJoin("secciones s")
                .leftJoin("cur.modalidadEstudio")
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

        StringBuilder strb = new StringBuilder();
        strb.append("Select gs.codigo ");
        strb.append("  from GrupoSeccion gs ");
        strb.append("  join gs.cicloAcademico cs ");
        strb.append(" where cs.id = :prm_ciclo ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_ciclo", cicloAcademico.getId());

        return query.list();
    }

    @Override
    public List<String> allCodigo2ByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder strb = new StringBuilder();
        strb.append("Select distinct substring(s.codigo2,1,3) as codigo ");
        strb.append("  from Seccion s ");
        strb.append("  join s.grupoSeccion gs ");
        strb.append("  join gs.cicloAcademico cs ");
        strb.append(" where cs.id = :prm_ciclo ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_ciclo", cicloAcademico.getId());

        return query.list();
    }

    @Override
    public List<GrupoSeccion> allByFilter(List<Long> ids, CicloAcademico ciclo, DepartamentoAcademico dpto, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca", "cur.departamentoAcademico da")
                .leftJoin("cur.planCalificacion pcc", "cur.planCalificacionRegular pcr")
                .orderBy("cur.nombre");

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
                .join("curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc", "secciones s", "cur.modalidadEstudio")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    public List<GrupoSeccion> allAbiertasByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc", "secciones s")
                .filter("gs.estado", EstadoGrupoSeccionEnum.ABI)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    public List<GrupoSeccion> allCerradasByCiclo(List<CicloAcademico> ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca")
                .leftJoin("planCalificacion pc", "secciones s")
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
        Octavia subQuerySearch = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion se", "se.grupoSeccion ggss")
                .left("doc.persona per", "se.grupoHoras gh", "se.aula au");

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "anexoBoletin ab", "curso cu")
                .leftJoin("ab.anexoSuperior abs", "planCalificacion pc")
                .filter("ca.id", ciclo)
                .searchFields("cu.nombre", "cu.codigo")
                .searchSubquery(subQuerySearch)
                .subqueryLinkedBy("gs.id", "ggss.id")
                .searchSubqueryFields("doc.codigo", "se.codigo2", "gh.codigo", "au.codigo")
                .searchSubqueryComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchSubqueryComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");

        Octavia subQueryLetra = null;
        boolean existeLetra = existeLetra(filter);
        if (existeLetra) {
            subQueryLetra = Octavia.query()
                    .from(Seccion.class, "sec")
                    .join("sec.grupoSeccion gpoSec", "sec.aula au", "au.oficinaSupervisora ofi")
                    .join("sec.grupoHoras gpo")
                    .filter("ofi.codigo", OficinaEnum.OERA);

            this.setLetra(filter, subQueryLetra);

            sql.exists(subQueryLetra);
            sql.linkedBy("gs.id", "gpoSec.id");
        }

        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);
        this.setOrder(filter, sql);

        return sql.all(getCurrentSession());
    }

    private boolean existeLetra(DynatableFilter filter) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return false;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("letra")) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void setLetra(DynatableFilter filter, Octavia subQueryLetra) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("letra")) {
                continue;
            }
            subQueryLetra.filter("gpo.letra", queries.get(key));
        }
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

    private void setOrder(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            sql.orderBy("gs.id desc");
            return;
        }
        for (String key : queries.keySet()) {
            if (key.equals("order-codigo")) {
                sql.orderBy("gs.codigo2");
                return;
            }
            if (key.equals("order-id")) {
                sql.orderBy("gs.id asc");
                return;
            }
        }
        sql.orderBy("gs.id desc");
        return;

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
    public void deleteGrupoSeccion(GrupoSeccion gpoSeccion) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  GrupoSeccion where id=:prm_id");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_id", gpoSeccion.getId());
        query.executeUpdate();
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
                .leftJoin("planCalificacion pc", "cur.modalidadEstudio", "cur.departamentoAcademico")
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<GrupoSeccion> allActivoByCursoCiclo(Curso curso, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca")
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("cur.id", curso)
                .filter("ca.id", ciclo);

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

    @Override
    public Map<Long, Long> allCountAlumnos(List<GrupoSeccion> grupos) {
        List<Long> gruposIds = new ArrayList<>();
        for (GrupoSeccion grupo : grupos) {
            gruposIds.add(grupo.getId());
        }
        StringBuilder strb = new StringBuilder();
        strb.append("Select gsec.id,count(ms) from MatriculaSeccion ms");
        strb.append(" inner join ms.seccion sec ");
        strb.append(" inner join sec.grupoSeccion gsec ");
        strb.append(" inner join ms.seccion sec ");
        strb.append(" where  ms.estado=:MSEC_ESTADO");
        strb.append(" and sec.tipoSeccion != :TIPO_SEC_DIF ");
        strb.append(" and gsec.id in (:GRUPOS) ");
        strb.append(" group by gsec.id ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("MSEC_ESTADO", EstadoMatriculaEnum.MAT.name());
        query.setParameter("TIPO_SEC_DIF", TipoSeccionEnum.PCUR.name());
        query.setParameterList("GRUPOS", gruposIds);
        List<Object[]> result = query.list();

        Map<Long, Long> resultados = new HashMap<>();
        for (Object[] object : result) {
            resultados.put(TypesUtil.getLong(object[0]), TypesUtil.getLong(object[1]));
        }
        return resultados;
    }

    @Override
    public Map<Long, Long> allCountAlumnosWithNf(List<GrupoSeccion> grupos) {
        List<Long> gruposIds = new ArrayList<>();
        for (GrupoSeccion grupo : grupos) {
            gruposIds.add(grupo.getId());
        }

        StringBuilder strb = new StringBuilder();
        strb.append(" Select ");
        strb.append(" gsec.id, ");
        strb.append(" count(*) ");
        strb.append(" from aca_matricula_curso mcur ");
        strb.append(" inner join aca_matricula_resumen mres on mcur.id_matricula_resumen=mres.id ");
        strb.append(" inner join aca_matricula_seccion msec on msec.id_matricula_resumen=mres.id ");
        strb.append(" inner join aca_seccion sec on sec.id=msec.id_seccion ");
        strb.append(" inner join aca_grupo_seccion gsec on sec.id_grupo_seccion=gsec.id and gsec.id_curso=mcur.id_curso");
        strb.append(" where  mcur.porcentaje_avance_nota=100 ");
        strb.append(" and msec.estado=:MSEC_ESTADO ");
        strb.append(" AND sec.tipo_seccion !=:TIPO_SEC_DIF ");
        strb.append(" and gsec.id in (:GRUPOS) ");
        strb.append(" group by gsec.id ");

        Query query = getCurrentSession().createSQLQuery(strb.toString());
        query.setParameter("MSEC_ESTADO", EstadoMatriculaEnum.MAT.name());
        query.setParameter("TIPO_SEC_DIF", TipoSeccionEnum.PCUR.name());
        query.setParameterList("GRUPOS", gruposIds);

        String listString = gruposIds.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        logger.debug("Grupos Ids {}", listString);

        List<Object[]> result = query.list();

        Map<Long, Long> resultados = new HashMap<>();
        for (Object[] object : result) {
            resultados.put(TypesUtil.getLong(object[0]), TypesUtil.getLong(object[1]));
        }
        return resultados;
    }

    @Override
    public List<GrupoSeccion> allByDynatableGruposSeccion(DynatableFilter filter, CicloAcademico ciclo, List<GrupoSeccion> gpos) {
        Octavia subQuery = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion se", "se.grupoSeccion ggss")
                .left("doc.persona per", "se.grupoHoras gh", "se.aula au");

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca", "curso cu")
                .leftJoin("anexoBoletin ab", "ab.anexoSuperior abs", "planCalificacion pc")
                .filter("ca.id", ciclo)
                .in("gs.id", gpos)
                .searchFields("cu.nombre", "cu.codigo", "ab.nombre", "abs.nombre")
                .searchSubquery(subQuery)
                .subqueryLinkedBy("gs.id", "ggss.id")
                .searchSubqueryFields("doc.codigo", "se.codigo2", "gh.codigo", "au.codigo")
                .searchSubqueryComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchSubqueryComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("gs.id asc");

        Octavia subQueryLetra = null;
        boolean existeLetra = existeLetra(filter);
        if (existeLetra) {
            subQueryLetra = Octavia.query()
                    .from(Seccion.class, "sec")
                    .join("sec.grupoSeccion gpoSec", "sec.aula au", "au.oficinaSupervisora ofi")
                    .join("sec.grupoHoras gpo")
                    .filter("ofi.codigo", OficinaEnum.OERA);

            this.setLetra(filter, subQueryLetra);

            sql.exists(subQueryLetra);
            sql.linkedBy("gs.id", "gpoSec.id");
        }

        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);

        return sql.all(getCurrentSession());
    }

    @Override
    public Long contarByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(GrupoSeccion.class, "gs")
                .join("cicloAcademico ca")
                .filter("ca.id", ciclo);

        return (Long) sql.find(getCurrentSession());
    }

    @Override
    public List<GrupoSeccion> allWithDocenteSeccionActivosByCiclo(CicloAcademico ciclo) {

        Octavia subquery = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion ggss")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("sec.matriculados", ">", 0);

        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca", "anexoBoletin ab")
                .join("ab.anexoSuperior asu")
                .filter("gs.estado", EstadoCursoCachimboEnum.ACT.name())
                .filter("ca.id", ciclo)
                .exists(subquery)
                .linkedBy("gs.id", "ggss.id")
                .orderBy("asu.orden", "ab.orden", "cur.nombre");

        return all(sql);

    }

    @Override
    public List<GrupoSeccion> allOrdenadoByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca", "anexoBoletin ab")
                .join("ab.anexoSuperior asu")
                .filter("ca.id", ciclo)
                .orderBy("asu.orden", "ab.orden", "cur.nombre");

        return all(sql);

    }

    @Override
    public List<GrupoSeccion> allOrdenadoByCicloAndAnexoBoletin(CicloAcademico ciclo, AnexoBoletin... anexoBol) {
        List<AnexoBoletin> anexosBoletin = Arrays.asList(anexoBol);
        Octavia sql = Octavia.query()
                .from(GrupoSeccion.class, "gs")
                .join("curso cur", "cicloAcademico ca", "anexoBoletin ab")
                .join("ab.anexoSuperior asu")
                .filter("ca.id", ciclo)
                .in("ab.id", anexosBoletin)
                .filter("gs.estado", SeccionEstadoEnum.ACT)
                .orderBy("gs.codigo2");

        return all(sql);

    }

    @Override
    public void setCodigo2Null(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update ").append(GrupoSeccion.class.getSimpleName());
        sql.append("    set codigo2 = null ");
        sql.append("  where cicloAcademico.id = :CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());

        query.executeUpdate();
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();

        sql.append(" DELETE ")
                .append(GrupoSeccion.class.getName()).append(" gs ")
                .append(" WHERE gs.cicloAcademico.id=:CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public void updateCodigo2(List<GrupoSeccion> gpoSecciones) {
        StringBuilder sql = new StringBuilder("update GrupoSeccion set codigo2 = case \n");
        for (GrupoSeccion gs : gpoSecciones) {
            sql.append(" when id = ").append(gs.getId()).append(" then '").append(gs.getCodigo2()).append("' \n");
        }
        sql.append(" end where id in (\n");
        int loop = 0;
        for (GrupoSeccion gs : gpoSecciones) {
            if (loop > 0) {
                sql.append(",");
            }
            sql.append(gs.getId());
            loop++;
        }
        sql.append(")");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.executeUpdate();
    }

}
