package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.CER;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.CFG;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.DES;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum.CRE;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;

@Repository
public class CicloAcademicoDAOH extends AbstractEasyDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }

    @Override
    public CicloAcademico find(long cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("id", cicloAcademico);

        return find(sql);
    }

    @Override
    public CicloAcademico findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("codigo", codigo);
        return find(sql);
    }

    @Override
    public CicloAcademico findActivo(ModalidadEstudio modalidad) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("estado", CicloAcademicoEstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public CicloAcademico findActivo(ModalidadEstudioEnum modalidadEnum) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.codigo", modalidadEnum.name())
                .filter("estado", CicloAcademicoEstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allActivesByModalidad(ModalidadEstudio modalidad, String[] orderBy) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("estado", CicloAcademicoEstadoEnum.ACT)
                .orderBy(orderBy);
        return all(sql);
    }

    @Override
    public List<CicloAcademico> allForChanges(Integer maxResultado, ModalidadEstudio modalidad) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(maxResultado);

        return all(sql);
    }

    @Override
    public CicloAcademico findAnteriorRegular(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("codigo", "<", ciclo.getCodigo())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public CicloAcademico findAnteriorActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("codigo", "<", ciclo.getCodigo())
                .filter("estado", "!=", DES)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", DES)
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", DES)
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo, ModalidadEstudioEnum modalidadEstudioAlumnoEnum) {

        if (modalidadEstudioAlumnoEnum == ModalidadEstudioEnum.ESP) {
            modalidadEstudioAlumnoEnum = ModalidadEstudioEnum.EPG;
        }
        if (modalidadEstudioAlumnoEnum == ModalidadEstudioEnum.VIS) {
            modalidadEstudioAlumnoEnum = ModalidadEstudioEnum.PRE;
        }
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio mest")
                .filter("tipo", "REG")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", DES)
                .filter("mest.codigo", modalidadEstudioAlumnoEnum)
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allActivos() {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("estado", CicloAcademicoEstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allWithInitAndOrderBy(int yearIni, String orderBy, CicloAcademicoEstadoEnum... cicloAcademicoEstadoEnum) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("ca.year", ">=", yearIni)
                .in("ca.estado", cicloAcademicoEstadoEnum)
                .orderBy(orderBy);

        return all(sql);
    }

    @Override
    public CicloAcademico findSiguienteNivelacionActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "NIV")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", DES)
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allUltimos(Integer cantidadCiclos) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .filter("tipo", "REG")
                .orderBy("year desc", "numeroCiclo desc")
                .limit(cantidadCiclos);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allUltimosByNext(Integer cantidadCiclos, List<CicloAcademico> actives) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, PEND, CFG, CRE));
        // .filter("tipo", "REG");
        for (CicloAcademico active : actives) {
            sql.filter("ca.codigo", ">", active.getCodigo());
        }
        sql.orderBy("year desc", "numeroCiclo desc");
        sql.limit(cantidadCiclos);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allPregradoByRange(int yearinit, int yearend) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .filter("year", ">", yearinit)
                .filter("year", "<", yearend)
                .filter("tipo", TipoCicloEnum.REG)
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC");

        return all(sql);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public List<CicloAcademico> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .searchFields("ca.descripcion", "ca.descripcion2", "ca.descripcion3", "ca.codigo", "ca.numeroCiclo", "ca.year", "me.nombre", "me.codigo")
                .orderBy("ca.codigo desc");
        sql.beginRelativeFilters();
        this.setModalidadEstudio(filter, sql);
        this.setPeriodo(filter, sql);
        return sql.all(getCurrentSession());
    }

    private void setModalidadEstudio(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        if (queries.get("modalidad") == null) {
            return;
        }
        sql.filter("me.id", queries.get("modalidad"));
    }

    private void setPeriodo(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        if (queries.get("periodo") == null) {
            return;
        }
        sql.filter("ca.year", queries.get("periodo"));
    }

    @Override
    public CicloAcademico findCicloAcademicoActivo() {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("estado", ACT);
        return find(sql);
    }

    @Override
    public CicloAcademico findCicloAcademicoActivoPRE() {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("estado", ACT)
                .filter("me.codigo", ModalidadEstudioEnum.PRE);
        return find(sql);
    }

    @Override
    public void updateActualizarBoletin(CicloAcademico cicloAcademico) {
        Octavia octavia = Octavia.update(CicloAcademico.class);
        octavia.set(cicloAcademico, "actualizarBoletin");
        this.update(octavia);
    }

    @Override
    public CicloAcademico findCicloAcademicoActivoByModalidad(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidadEstudio)
                .filter("estado", ACT);
        return find(sql);
    }

    @Override
    public void updateFechaMatriculables(CicloAcademico cicloAcademico) {
        Octavia octavia = Octavia.update(CicloAcademico.class);
        octavia.set(cicloAcademico, "fechaMatriculables");
        this.update(octavia);
    }

    @Override
    public void updateFechaPrioridades(CicloAcademico cicloAcademico) {
        Octavia octavia = Octavia.update(CicloAcademico.class);
        octavia.set(cicloAcademico, "fechaPrioridades");
        this.update(octavia);
    }

    @Override
    public void updateFechaTurnosAsignados(CicloAcademico cicloAcademico) {
        Octavia octavia = Octavia.update(CicloAcademico.class);
        octavia.set(cicloAcademico, "fechaTurnosAsignados");
        this.update(octavia);
    }

    @Override
    public void updateFechasTurnosAignadosDisponibles(CicloAcademico cicloAcademico) {
        Octavia octavia = Octavia.update(CicloAcademico.class);
        octavia.set(cicloAcademico, "fechaTurnosAsignados");
        octavia.set(cicloAcademico, "fechaTurnosDisponibles");
        this.update(octavia);
    }

    @Override
    public CicloAcademico find(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public List<CicloAcademico> all() {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .orderBy("year desc", "numeroCiclo desc");
        return all(sql);
    }

    @Override
    public List<CicloAcademico> allCicloByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .beginBlock()
                .__().filter("ca.numeroCiclo", "like", nombre)
                .__().filter("ca.year", "like", nombre)
                .__().filter("ca.codigo", "like", nombre)
                .__().filter("ca.descripcion", "like", nombre)
                .__().filter("ca.descripcion2", "like", nombre)
                .__().filter("ca.descripcion3", "like", nombre)
                .endBlock()
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .limit(15);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allCicloByNameExceptList(String nombre, List<CicloAcademico> ciclos) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .notIn("ca.id", ciclos)
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .beginBlock()
                .__().filter("ca.numeroCiclo", "like", nombre)
                .__().filter("ca.year", "like", nombre)
                .__().filter("ca.codigo", "like", nombre)
                .__().filter("ca.descripcion", "like", nombre)
                .__().filter("ca.descripcion2", "like", nombre)
                .__().filter("ca.descripcion3", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public CicloAcademico findByCodigoModalidadEstudio(String codigo, ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("codigo", codigo)
                .filter("me.id", modalidad);
        return find(sql);
    }

    @Override
    public List<CicloAcademico> allByModalidadEstudioName(ModalidadEstudio modalidad, String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .beginBlock()
                .__().filter("ca.numeroCiclo", "like", nombre)
                .__().filter("ca.year", "like", nombre)
                .__().filter("ca.codigo", "like", nombre)
                .__().filter("ca.descripcion", "like", nombre)
                .__().filter("ca.descripcion2", "like", nombre)
                .__().filter("ca.descripcion3", "like", nombre)
                .endBlock()
                .limit(15);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allCicloByNameDescendent(String nombre, ModalidadEstudio modalidad) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .orderBy("ca.year desc", "ca.numeroCiclo desc")
                .beginBlock()
                .__().filter("ca.numeroCiclo", "like", nombre)
                .__().filter("ca.year", "like", nombre)
                .__().filter("ca.codigo", "like", nombre)
                .__().filter("ca.descripcion", "like", nombre)
                .__().filter("ca.descripcion2", "like", nombre)
                .__().filter("ca.descripcion3", "like", nombre)
                .endBlock()
                .limit(15);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allAnteriores(int ciclos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("ca.modalidadEstudio me")
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("me.id", cicloAcademico.getModalidadEstudio())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(ciclos);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("ca.modalidadEstudio me")
                .filter("ca.codigo", codigo);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allByEstados(List<String> estados) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("ca.modalidadEstudio me")
                .in("ca.estado", estados);

        return all(sql);
    }

    @Override
    public List<CicloAcademico> allByLikeName(String nombre, ModalidadEstudio modalidad, List<CicloAcademico> notInt, Integer limit) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidad)
                .beginBlock()
                .__().filter("ca.numeroCiclo", "like", nombre)
                .__().filter("ca.year", "like", nombre)
                .__().filter("ca.codigo", "like", nombre)
                .__().filter("ca.descripcion", "like", nombre)
                .__().filter("ca.descripcion2", "like", nombre)
                .__().filter("ca.descripcion3", "like", nombre)
                .endBlock()
                .notIn("ca.id", notInt)
                .limit(limit);
        return all(sql);
    }

    @Override
    public CicloAcademico findVerBoletin() {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("ca.verBoletin", true);
        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteConfOrAct(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("codigo", ">", cicloAcademico.getCodigo())
                .in("estado", Arrays.asList(CFG, ACT))
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> findAnteriorRegActivo(int ciclos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "REG")
                .filter("ca.codigo", "<", cicloAcademico.getCodigo())
                .filter("estado", "!=", DES)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(ciclos);
        return all(sql);
    }

    @Override
    public List<CicloAcademico> findCiclosVisibles(ModalidadEstudioEnum modalidadEstudioEnum) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("ca.modalidadEstudio me")
                .filter("me.codigo", modalidadEstudioEnum.name())
                .filter("ca.visibleLogin", 1);

        return all(sql);
    }
}
