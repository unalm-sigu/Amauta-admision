package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.ACTIVIDADES;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.INGRESANTE;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.POSTGRADO;
import static pe.edu.lamolina.model.enums.GrupoAnexoEnum.DPTO;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.anexoboletin.AnexoResumen;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;

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
                .isNotNull("abs.id");
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
                .leftJoin("anexoSuperior abs")
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
    public List<AnexoBoletin> allTodosByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("ab")
                .from(DocenteSeccion.class, "dsec")
                .join("dsec.seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico ci", "gs.curso cu")
                .join("gs.anexoBoletin ab", "ab.anexoSuperior abs")
                .filter("ci.id", ciclo)
                .filter("sec.estado", SeccionEstadoEnum.ACT.name())
                .filter("dsec.estado", EstadoEnum.ACT.name())
                .filter("cu.codigo", "<>", "CI0000");
        return all(sql);
    }

}
