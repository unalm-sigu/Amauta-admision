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
import static pe.edu.lamolina.model.enums.CicloEstadoEnum.CFG;
import static pe.edu.lamolina.model.enums.CicloEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.CicloEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.CicloEstadoEnum.CER;
import pe.edu.lamolina.model.enums.CicloEstadoEnum;

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
                .filter("estado", ACT);

        return find(sql);
    }

    @Override
    public List<CicloAcademico> allForChanges(Integer maxResultado) {

        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .orderBy("year desc", "numeroCiclo desc")
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
                .filter("estado", "!=", CicloEstadoEnum.DES.name())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);
        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", CicloEstadoEnum.DES.name())
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
                .filter("estado", "!=", CicloEstadoEnum.DES.name())
                .orderBy("ca.year asc", "ca.numeroCiclo asc")
                .limit(1);

        return find(sql);
    }

    @Override
    public CicloAcademico findSiguienteNivelacionActivo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .filter("tipo", "NIV")
                .filter("codigo", ">", ciclo.getCodigo())
                .filter("estado", "!=", CicloEstadoEnum.DES.name())
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
    public List<CicloAcademico> allCicloAcademicoByRange(int yearinit, int yearend) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .in("estado", Arrays.asList(ACT, CER, PEND, CFG))
                .filter("year", ">", yearinit)
                .filter("year", "<", yearend)
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
                .filter("estado", CicloEstadoEnum.ACT.name());
        return find(sql);
    }

    @Override
    public CicloAcademico findCicloAcademicoActivoByModalidad(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", modalidadEstudio)
                .filter("estado", CicloEstadoEnum.ACT.name());
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

}
