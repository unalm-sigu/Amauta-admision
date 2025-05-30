package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.resumen.DepartamentoCursosProgramadosDTO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.ACTIVIDADES;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.INGRESANTE;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.POSTGRADO;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.DPTO;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.amauta.controller.academico.anexoboletin.AnexoResumen;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Repository
public class AnexoBoletinDAOH extends AbstractEasyDAO<AnexoBoletin> implements AnexoBoletinDAO {

    public AnexoBoletinDAOH() {
        super();
        setClazz(AnexoBoletin.class);
    }

    @Override
    public AnexoBoletin find(long id) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs")
                .leftJoin("departamentoAcademico da", "carrera ca")
                .filter("ab.id", id);
        return find(sql);
    }

    @Override
    public AnexoBoletin findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs")
                .leftJoin("departamentoAcademico da", "carrera ca")
                .filter("ab.codigo", codigo);
        return find(sql);
    }

    @Override
    public AnexoResumen resumen() {
        Octavia sql = Octavia.query()
                .select(
                        "sum(case abs.id when " + INGRESANTE.getValue() + " then 1 else 0 end)",
                        "sum(case abs.id when " + DPTO.getValue() + " then 1 else 0 end)",
                        "sum(case abs.id when " + POSTGRADO.getValue() + " then 1 else 0 end)",
                        "sum(case abs.id when " + ACTIVIDADES.getValue() + " then 1 else 0 end)")
                .into(AnexoResumen.class)
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs");

        return (AnexoResumen) sql.find(getCurrentSession());
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
            String values = (String) queries.get(key);
            if (values.equals("ingresantes")) {
                sql.filter("ass.id", GrupoAnexoEnum.INGRESANTE.getValue());

            } else if (values.equals("departamentos")) {
                sql.filter("ass.id", GrupoAnexoEnum.DPTO.getValue());

            } else if (values.equals("posgrados")) {
                sql.filter("ass.id", GrupoAnexoEnum.POSTGRADO.getValue());

            } else if (values.equals("actividades")) {
                sql.filter("ass.id", GrupoAnexoEnum.ACTIVIDADES.getValue());
            }
        }
    }

    @Override
    public List<AnexoBoletin> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior ass")
                .leftJoin("departamentoAcademico da", "carrera ca")
                .searchFields("ab.nombre", "da.nombre")
                .orderBy("ass.orden", "ab.estado", "ab.orden");

        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);
        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allHijosByDynatable(DynatableFilter filter, CicloAcademico ciclo) {

        Long idSuperior = null;
        if (filter.getQueries() != null && filter.getQueries().get("departamentoSuperior") != null) {
            Object val = filter.getQueries().get("departamentoSuperior");
            try {
                idSuperior = val instanceof Number
                        ? ((Number) val).longValue()
                        : Long.parseLong(val.toString());
            } catch (NumberFormatException e) {
                System.out.printf("ERROR: %s\n", e.getMessage());
            }
        }

        DynatableSql sql = new DynatableSql(filter)
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "anexoSuperior abs")
                .isNotNull("abs.id")
                .filter("ab.estado", EstadoEnum.ACT)
                .searchFields("ab.nombre", "da.nombre","da.codigo")
                .orderBy("abs.orden", "ab.estado", "ab.orden");

        if (idSuperior != null) {
            sql.filter("abs.id", idSuperior);
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public List<DepartamentoCursosProgramadosDTO> allCursosProgramadosByAnexo(List<Long> ids, CicloAcademico cicloAcademico, AnexoBoletin anexo) {
        StringBuilder strb = new StringBuilder();
        strb.append(" SELECT ");
        strb.append("     bol.id as idAnexo, ");
//        strb.append("     da.nombre as nombreDepartamento, ");
        strb.append("     count(distinct ags.id_curso) as cantidadCursos, ");
        strb.append("     count(distinct ags.id) as cantidadGrupos, ");
        strb.append("     sum(if(sec.estado='ACT',1,0)) as activos, ");
        strb.append("     sum(if(sec.estado='ANU',1,0)) as anulados, ");
        strb.append("     sum(if(sec.estado='CAN',1,0)) as cancelados, ");
        strb.append("     sum(if(sec.estado='FUS',1,0)) as fusionados, ");
        strb.append("     sum(if(sec.estado='INA',1,0)) as inactivos, ");
        strb.append("     sum(if(sec.estado='BLO',1,0)) as bloqueados, ");
        strb.append("     count(*) as totalSecciones, ");
        strb.append("     SUM(CASE ");
        strb.append("       WHEN sec.matriculados < 6 THEN 1 ");
        strb.append("       ELSE 0 ") ;
        strb.append("     END) AS cursosMenos6Alumnos, ");
        strb.append("     SUM(CASE ");
        strb.append("       WHEN sec.estado = 'ACT' AND ( ");
        strb.append("               SELECT COUNT(*) ");
        strb.append("       FROM aca_docente_seccion dsec ");
        strb.append("        LEFT JOIN aca_docente doc ON dsec.id_docente = doc.id ");
        strb.append("       WHERE dsec.id_seccion = sec.id AND doc.id_persona IS NULL ");
        strb.append("       ) > 0 THEN 1 ");
        strb.append("       ELSE 0 ");
        strb.append("       END) AS cursosSinDocente ");
        strb.append(" FROM aca_anexo_boletin bol ");
        strb.append(" join aca_grupo_seccion ags on bol.id = ags.id_anexo_boletin");
        strb.append(" join aca_seccion sec on ags.id = sec.id_grupo_seccion ");
        strb.append(" join aca_ciclo_academico aca on ags.id_ciclo = aca.id ");
        strb.append(" join aca_curso cur on ags.id_curso = cur.id ");
        strb.append(" left join aca_departamento_academico ada on cur.id_departamento_academico = ada.id ");
        strb.append(" where aca.id = :prm_ciclo ");
        strb.append(" and bol.id in (:prm_departamentos) ");

        if (anexo != null) {
            strb.append(" and bol.id = :prm_departamento ");
        }
        strb.append(" and sec.tipo_seccion <> 'TCUR' ");

        strb.append(" GROUP BY bol.id ");

        SQLQuery query = getCurrentSession().createSQLQuery(strb.toString());
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

        query.setParameter("prm_ciclo", cicloAcademico.getId());
        if (anexo != null) {
            query.setParameter("prm_departamento", anexo.getId());
        }
        query.setParameterList("prm_departamentos", ids);

        List<DepartamentoCursosProgramadosDTO> result = new ArrayList<>();
        List<Map> lstData = query.list();

        for (Map map : lstData) {
            result.add(new DepartamentoCursosProgramadosDTO(
                    ((Number) map.get("idAnexo")).longValue(),
                    ((Number) map.get("cantidadCursos")).longValue(),
                    ((Number) map.get("cantidadGrupos")).longValue(),
                    ((Number) map.get("activos")).longValue(),
                    ((Number) map.get("anulados")).longValue(),
                    ((Number) map.get("cancelados")).longValue(),
                    ((Number) map.get("fusionados")).longValue(),
                    ((Number) map.get("inactivos")).longValue(),
                    ((Number) map.get("bloqueados")).longValue(),
                    ((Number) map.get("totalSecciones")).longValue(),
                    ((Number) map.get("cursosMenos6Alumnos")).longValue(),
                    ((Number) map.get("cursosSinDocente")).longValue()
            ));
        }

        return result;
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "carrera ca", "anexoSuperior abs")
                .isNull("abs.id");
        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperioresOrderedbyOrden() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "carrera ca", "anexoSuperior abs")
                .isNull("abs.id")
                .orderBy("ab.orden");
        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allAnexosHijos() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "carrera ca", "anexoSuperior abs")
                .isNotNull("abs.id")
                .orderBy("ab.estado", "ab.orden");
        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allActivosHijos() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs")
                .leftJoin("departamentoAcademico da")
                .filter("ab.estado", EstadoEnum.ACT)
                .orderBy("abs.orden", "ab.orden");

        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allBySuperiorCiclo(AnexoBoletin anexoSuperior, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("ab")
                .from(GrupoSeccion.class, "gs")
                .join("anexoBoletin ab", "ab.anexoSuperior abs", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .orderBy("ab.nombre");

        if (anexoSuperior.getId() != 0) {
            sql.filter("abs.id", anexoSuperior);
        }

        return all(sql);
    }

    @Override
    public List<AnexoBoletin> all() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("anexoSuperior abs", "departamentoAcademico dpto", "carrera car")
                .leftJoin("car.facultad", "car.modalidadEstudio", "dpto.facultad")
                .orderBy("ab.nombre");

        return all(sql);
    }

    @Override
    public AnexoBoletin findActivoByOrdenAnexoSuperior(Integer orden, AnexoBoletin anexoSuperior) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs")
                .filter("abs.id", anexoSuperior)
                .filter("ab.orden", orden)
                .filter("ab.estado", EstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public AnexoBoletin findDepartamento(DepartamentoAcademico departamentoAcademico) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs", "departamentoAcademico da")
                .filter("ab.estado", EstadoEnum.ACT)
                .filter("abs.id", AcademicoConstantine.ANEXO_SUP_DEP_ACAD)
                .filter("da.id", departamentoAcademico);

        return find(sql);
    }

    @Override
    public List<AnexoBoletin> allBySuperior(AnexoBoletin anexoSuperior) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .left("anexoSuperior abs")
                .filter("abs.id", anexoSuperior)
                .orderBy("ab.orden");

        return all(sql);
    }

    @Override
    public List<AnexoBoletin> countGpoSeccByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("ab.id", "count(*)")
                .into(AnexoBoletin.class)
                .from(GrupoSeccion.class, "gs")
                .left("anexoBoletin ab", "cicloAcademico ca")
                .filter("ca.id", ciclo)
                .groupBy("ab.id");

        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allHijosWithCursos(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("ab")
                .from(DocenteSeccion.class, "dsec")
                .join("dsec.seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico ci")
                .join("gs.anexoBoletin ab")
                .leftJoin("ab.departamentoAcademico da", "ab.carrera ca", "ab.anexoSuperior abs")
                .filter("ci.id", ciclo)
                .filter("sec.estado", SeccionEstadoEnum.ACT.name())
                .filter("dsec.estado", EstadoEnum.ACT.name())
                .isNotNull("abs.id");
        return all(sql);
    }

    @Override
    public List<AnexoBoletin> allTodosByCiclo(CicloAcademico ciclo, List<AnexoBoletin> anexosSuperiores, List<AnexoBoletin> anexosInferiores) {
        Octavia sql = Octavia.query()
                .selectDistinct("ab")
                .from(DocenteSeccion.class, "dsec")
                .join("dsec.seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico ci", "gs.curso cu")
                .join("gs.anexoBoletin ab", "ab.anexoSuperior abs")
                .filter("ci.id", ciclo)
                .filter("sec.estado", SeccionEstadoEnum.ACT.name())
                .filter("dsec.estado", EstadoEnum.ACT.name())
                .filter("cu.codigo", "<>", "CI0000");

        if (anexosSuperiores != null) {
            sql.in("abs.id", anexosSuperiores);
        }
        if (anexosInferiores != null && !anexosInferiores.isEmpty()) {
            sql.in("ab.id", anexosInferiores);
        }

        return all(sql);
    }

}
