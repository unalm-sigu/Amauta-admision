package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Repository
public class GrupoHorasDAOH extends AbstractEasyDAO<GrupoHoras> implements GrupoHorasDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GrupoHorasDAOH() {
        super();
        setClazz(GrupoHoras.class);
    }

    @Override
    public GrupoHoras findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .filter("gh.codigo", codigo);

        return find(sql);
    }

    @Override
    public GrupoHoras findByCodeTipoCiclo(String codigo, TipoCicloEnum tipoCicloEnum) {
        List<String> tiposCiclos = new ArrayList<>();
        tiposCiclos.add(tipoCicloEnum.name());
        //tiposCiclos.add(TipoCicloEnum.AMB.name());
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .join("tipoGrupoHoras tgh")
                .filter("codigo", codigo);
        return find(sql);
    }

    @Override
    public GrupoHoras findGrupoHorasByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .filter("codigo", codigo);

        return find(sql);
    }

    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter, CicloAcademico ciclo) {
        Octavia subquery = Octavia.query()
                .from(GrupoHorasExcluido.class, "ghe")
                .join("grupoHoras gpo", "cicloAcademico cic")
                .filter("cic.id", ciclo);

        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHoras.class, "gh")
                .leftJoin("tipoGrupoHoras tgh")
                .notExists(subquery)
                .linkedBy("gh.id", "gpo.id")
                .searchFields("codigo", "letra");

        this.setCondicionQueries(filter, sql);

        return all(sql);
    }

    private void setCondicionQueries(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            sql.orderBy("gh.id desc");
            return;
        }

        boolean conOrden = false;
        for (String key : queries.keySet()) {
            if (!key.equals("order-letra")) {
                continue;
            }

            String values = (String) queries.get(key);
            if (values.equals("alfa")) {
                conOrden = true;
                sql.orderBy("gh.letra", "gh.codigo");
            }
        }
        if (!conOrden) {
            sql.orderBy("gh.id desc");
        }

        for (String key : queries.keySet()) {
            if (!key.equals("tipo-grupo")) {
                continue;
            }

            String values = (String) queries.get(key);
            sql.filter("tgh.id", values);
        }
    }

    @Override
    public GrupoHoras find(GrupoHoras grupoHoras) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .leftJoin("diaHoraGrupo dhg")
                .leftJoin("tipoGrupoHoras tgh")
                .filter("grup.id", grupoHoras);

        return find(sql);
    }

    @Override
    public List<GrupoHoras> allByTipoGrupoHora(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("dia", "hora")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allByTipoGrupoHoraAndCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("dia", "hora")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.tipo", tipoGrupoHorasEnum)
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allByTipoGpoDynatable(DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico,
            List<GrupoHoras> grupoHorasFilter) {

        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ca.id", cicloAcademico)
                .in("gh.id", grupoHorasFilter)
                .searchFields("gh.codigo");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allZetasByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHoras.class, "gh")
                .join("gh.tipoGrupoHoras tgh")
                .filter("tgh.tipo", TipoGrupoHorasEnum.ZETA)
                .searchFields("gh.codigo");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<GrupoHoras> allRegulares() {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .join("tipoGrupoHoras tgh")
                .filter("gh.tipoSeccion", TipoSeccionEnum.TEO)
                .filter("tgh.tipo", TipoGrupoHorasEnum.REGULAR)
                .orderBy("gh.codigo");

        return all(sql);
    }

    @Override
    public List<GrupoHoras> allGrupoHoras(List<Long> gruposHoras) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .join("tipoGrupoHoras")
                .in("gh.id", gruposHoras)
                .notIn("gh.id", AcademicoConstantine.GRUPOS_HORAS_UNUSED)
                .orderBy("gh.codigo");
        return all(sql);
    }

    @Override
    public List<GrupoHoras> searchByNombreFilter(String nombre, Integer limit) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .join("tipoGrupoHoras tgh")
                .filter("gh.codigo", "like", nombre)
                .orderBy("gh.codigo")
                .limit(limit);

        return sql.all(getCurrentSession());
    }

    @Override
    public Map<Long, Integer> countAlumnosGroupByGrupoHoras(List grupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .select("gh.id", "count(distinct mr)")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("mr.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "alu.carrera carr", "carr.facultad fac")
                .join("sec.grupoHoras gh")
                .leftJoin("per.tipoDocumento tdoc")
                .in("gh.id", grupoHoras)
                .filter("ca.id", cicloAcademico)
                .filter("ms.estado", EstadoMatriculaEnum.MAT)
                .groupBy("gh.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public List<GrupoHoras> allByLetrasAndTipoGrupoHoras(List<String> letras, TipoSeccionEnum tipoSeccionEnum, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .join("tipoGrupoHoras tgh")
                .in("gh.letra", letras)
                .filter("tgh.tipo", tipoGrupoHorasEnum.name())
                .filter("gh.tipoSeccion", tipoSeccionEnum.name())
                .orderBy("gh.codigo");
        return all(sql);
    }

    @Override
    public Map<Long, Long> allGruposCountBySemanaExamen(SemanaExamen semanaExamen,
            CicloAcademico cicloAcademico,
            TipoGrupoHorasEnum tipoGrupoHorasEnum,
            Integer horasForDay) {
        Octavia sql = Octavia.query()
                .select("gh.id", "d.id", "count(dhg)")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "cicloAcademico ca", "dia d", "hora h")
                .join("gh.tipoGrupoHoras tgh")
                .filter("ca.id", cicloAcademico)
                .filter("tgh.tipo", tipoGrupoHorasEnum)
                .filter("h.numero", ">=", semanaExamen.getHoraInicio().getNumero())
                .filter("h.numero", "<", semanaExamen.getHoraFin().getNumero())
                .groupBy("gh.id", "d.id");
        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Long> result = new HashMap();
        for (Object[] objects : resultado) {
            if (TypesUtil.getInt(objects[2]) >= horasForDay) {
                result.put(TypesUtil.getLong(objects[0]), TypesUtil.getLong(objects[1]));
            }
        }
        return result;
    }

    @Override
    public List<GrupoHoras> allByTipoCiclo(TipoCicloEnum tipoCiclo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .filter("gh.tipoCiclo", tipoCiclo)
                .orderBy("gh.codigo");

        return all(sql);
    }

    @Override
    public List<GrupoHoras> allByTipoCiclo(String tipoCiclo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .filter("gh.tipoCiclo", tipoCiclo)
                .notIn("gh.id", AcademicoConstantine.GRUPOS_HORAS_UNUSED)
                .orderBy("gh.codigo");

        return all(sql);
    }

    @Override
    public List<GrupoHoras> allSimples() {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .orderBy("gh.letra", "gh.codigo");

        return all(sql);
    }

}
