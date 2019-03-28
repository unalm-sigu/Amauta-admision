package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.VIS;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraResumen;

@Repository
public class CarreraDAOH extends AbstractEasyDAO<Carrera> implements CarreraDAO {

    public CarreraDAOH() {
        super();
        setClazz(Carrera.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Carrera findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .filter("ca.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Carrera> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .searchFields("ca.nombre", "ca.codigo")
                .orderBy("ca.id desc");
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
            if (!key.equals("me.codigo")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("pregrados")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.PRE.name());

            } else if (values.equals("posgrados")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.EPG.name());

            } else if (values.equals("especiales")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.ESP.name());

            } else if (values.equals("visitantes")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.VIS.name());
            }
        }
    }

    @Override
    public List<Carrera> allByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio mo", "mo.compania co")
                .filter("co.id", compania);

        return all(sql);
    }

    @Override
    public Carrera find(Long id) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.id", id);

        return find(sql);
    }

    @Override
    public List<Carrera> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.nombre", "like", nombre);

        return all(sql);
    }

    @Override
    public List<Carrera> allByModalidadEstudioNombre(String codigoEstudio, String nombre) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("me.codigo", codigoEstudio)
                .filter("ca.nombre", "like", nombre);

        return all(sql);
    }

    @Override
    public CarreraResumen resumen() {
        Octavia sql = Octavia.query()
                .select("sum(case me.codigo when '" + PRE.name() + "' then 1 else 0 end)",
                        "sum(case me.codigo when '" + EPG.name() + "' then 1 else 0 end)",
                        "sum(case me.codigo when '" + ESP.name() + "' then 1 else 0 end)",
                        "sum(case me.codigo when '" + VIS.name() + "' then 1 else 0 end)")
                .into(CarreraResumen.class)
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me");

        return (CarreraResumen) sql.find(getCurrentSession());
    }

    @Override
    public List<Carrera> allByFilter(Facultad facultad, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("facultad fa")
                .filter("fa.id", facultad)
                .filter("ca.estado", estadoEnum);

        return all(sql);
    }

    @Override
    public List<Carrera> allRegularesByCarreras(List<Carrera> carreras) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .in("me.codigo", Arrays.asList(PRE, EPG))
                .in("ca.id", carreras)
                .orderBy("ca.nombre");

        return all(sql);
    }

    @Override
    public List<Carrera> all() {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .orderBy("ca.codigo desc");

        return all(sql);
    }

    @Override
    public List<Carrera> allByNombreModalidad(String nombre, ModalidadEstudio modalidadEstudio) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa")
                .filter("car.estado", EnteAcademicoEstadoEnum.ACT)
                .filter("car.nombre", "like", nombre)
                .filter("me.id", modalidadEstudio)
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Carrera> allByModalidad(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                //.filter("ca.estado", EstadoCarreraEnum.ACT)
                .filter("me.id", modalidadEstudio)
                .orderBy("ca.codigo");

        return all(sql);
    }

    @Override
    public List<Carrera> allActivasByModalidad(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EnteAcademicoEstadoEnum.ACT)
                .filter("me.id", modalidadEstudio)
                .orderBy("ca.codigo desc");

        return all(sql);
    }

    @Override
    public List<Carrera> allByNombreModalidad(String nombre, List<ModalidadEstudio> modalidadEstudio) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa")
                .filter("car.estado", EnteAcademicoEstadoEnum.ACT)
                .filter("car.nombre", "like", nombre)
                .in("me.id", modalidadEstudio)
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Carrera> allByNombre(String nombre, Compania cia) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa", "me.compania co")
                .filter("car.estado", EnteAcademicoEstadoEnum.ACT)
                .filter("car.nombre", "like", nombre)
                .filter("co.id", cia)
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Carrera> allActivas() {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EnteAcademicoEstadoEnum.ACT);
        return all(sql);
    }

    @Override
    public List<Carrera> allActivasByModalidades(List<String> modalidadesCodes) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EnteAcademicoEstadoEnum.ACT)
                .in("me.codigo", modalidadesCodes);

        return all(sql);
    }

    @Override
    public List<Carrera> allByModalidadEnum(ModalidadEstudioEnum modalidad) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.codigo", modalidad)
                .orderBy("ca.nombre");
        return all(sql);
    }

    @Override
    public List<Carrera> allActivasByModalidadEnum(ModalidadEstudioEnum modalidad) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EnteAcademicoEstadoEnum.ACT)
                .filter("me.codigo", modalidad);
        return all(sql);
    }

    @Override
    public List<Carrera> allOficinaAndIds(List<Long> idEsp) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("facultad fa")
                .in("fa.id", idEsp);
        return all(sql);
    }

    @Override
    public List<Carrera> allPregradoByCicloMatriculables(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("carr")
                .from(MatriculaResumen.class, "mr")
                .join("mr.alumno al", "al.modalidadEstudio me", "al.carrera carr", "mr.cicloAcademico ci", "carr.facultad")
                .filter("ci.id", ciclo)
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .orderBy("carr.nombre");

        return all(sql);
    }

    @Override
    public List<Carrera> allByMatriculablesCicloFacultades(List<Facultad> facultades, CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .selectDistinct("carr")
                .from(MatriculaResumen.class, "mr")
                .join("mr.alumno al", "al.modalidadEstudio me", "al.carrera carr", "mr.cicloAcademico ci", "carr.facultad fac")
                .filter("ci.id", ciclo)
                .in("fac.id", facultades)
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .orderBy("carr.nombre");

        return all(sql);

    }

    @Override
    public List<Carrera> allByCarreras(List<Carrera> carreras) {

        Octavia sql = Octavia.query()
                .from(Carrera.class, "carr")
                .in("carr.id", carreras)
                .orderBy("carr.nombre");

        return all(sql);

    }

    @Override
    public List<Carrera> allByMatriculablesCicloCarreras(List<Carrera> carreras, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("carr")
                .from(MatriculaResumen.class, "mr")
                .join("mr.alumno al", "al.modalidadEstudio me", "al.carrera carr", "mr.cicloAcademico ci")
                .filter("ci.id", ciclo)
                .in("carr.id", carreras)
                .filter("me.codigo", ModalidadEstudioEnum.PRE)
                .orderBy("carr.nombre");

        return all(sql);

    }

}
