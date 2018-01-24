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
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoCarreraEnum;
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
        return (Carrera) sql.find(getCurrentSession());
    }

    @Override
    public List<Carrera> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.nombre", "like", nombre);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allByModalidadEstudioNombre(String codigoEstudio, String nombre) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("me.codigo", codigoEstudio)
                .filter("ca.nombre", "like", nombre);
        return sql.all(getCurrentSession());
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
    public List<Carrera> allCarrera() {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .orderBy("ca.codigo desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa")
                .filter("car.estado", EstadoCarreraEnum.ACT)
                .filter("car.nombre", "like", nombre)
                .filter("me.id", modalidadEstudio)
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allCarreraByModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EstadoCarreraEnum.ACT)
                .filter("me.id", modalidadEstudio)
                .orderBy("ca.codigo");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allActivoByModalidad(ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .filter("ca.estado", EstadoCarreraEnum.ACT)
                .filter("me.id", modalidadEstudio)
                .orderBy("ca.codigo desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Carrera> allCarreraByNameAndModalidad(String nombre, List<ModalidadEstudio> modalidadEstudio) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa")
                .filter("car.estado", EstadoCarreraEnum.ACT)
                .filter("car.nombre", "like", nombre)
                .in("me.id", modalidadEstudio)
                .limit(15);
        return sql.all(getCurrentSession());
    }

}
