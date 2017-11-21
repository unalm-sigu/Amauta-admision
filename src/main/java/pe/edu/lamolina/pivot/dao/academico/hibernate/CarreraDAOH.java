package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraResumen;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;

@Repository
public class CarreraDAOH extends AbstractDAO<Carrera> implements CarreraDAO {

    public CarreraDAOH() {
        super();
        setClazz(Carrera.class);
    }

    @Override
    public Carrera findByCodigo(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .filter("ca.codigo", codigo);
        return this.find(sqlUtil);
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
        SqlUtil sqlUtil = new SqlUtil("ca")
                .parents("modalidadEstudio mo", "_mo.compania co")
                .filter("co.id", compania);
        return all(sqlUtil);
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
        StringBuilder sql = new StringBuilder();
        sql.append("select new ").append(CarreraResumen.class.getName());
        sql.append(" (   ");
        sql.append("   sum(case me.codigo when :PRE then 1 else 0 end),   ");
        sql.append("   sum(case me.codigo when :EPG  then 1 else 0 end),   ");
        sql.append("   sum(case me.codigo when :ESP  then 1 else 0 end),   ");
        sql.append("   sum(case me.codigo when :VIS  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(Carrera.class.getName()).append(" as ca ");
        sql.append(" inner join  ca.modalidadEstudio me ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("PRE", ModalidadEstudioEnum.PRE.name());
        query.setString("EPG", ModalidadEstudioEnum.EPG.name());
        query.setString("ESP", ModalidadEstudioEnum.ESP.name());
        query.setString("VIS", ModalidadEstudioEnum.VIS.name());

        return (CarreraResumen) query.uniqueResult();
    }

    @Override
    public List<Carrera> allByFilter(Facultad facultad, EstadoEnum estadoEnum) {
        SqlUtil sqlUtil = new SqlUtil("ca")
                .parents("facultad fa")
                .filter("fa.id", facultad)
                .filter("ca.estado", estadoEnum.name());
        return all(sqlUtil);
    }

}
