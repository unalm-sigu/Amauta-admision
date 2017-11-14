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
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;

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
        Octavia subquery = Octavia.query()
                .from(AnexoBoletin.class, "as")
                .join("anexoSuperior ass", "carrera cax");

        DynatableSql sql = new DynatableSql(filter)
                .from(Carrera.class, "ca")
                .join("modalidadEstudio me", "facultad fa")
                .searchFields("ca.nombre", "ca.codigo")
                .searchSubquery(subquery)
                .subqueryLinkedBy("ca.id", "cax.id")
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
            if (!key.equals("ass.id")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("ingresantes")) {
                sql.filter("ass.id", GrupoAnexoEnum.INGRESANTE.getValue());

            } else if (values.equals("departamentos")) {
                sql.filter("ass.id", GrupoAnexoEnum.DPTO.getValue());

            } else if (values.equals("postGrados")) {
                sql.filter("ass.id", GrupoAnexoEnum.POSTGRADO.getValue());

            } else if (values.equals("actividades")) {
                sql.filter("ass.id", GrupoAnexoEnum.ACTIVIDADES.getValue());
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
        sql.append("   sum(case abs.id when :INGRE then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :DPTO  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :POST  then 1 else 0 end),   ");
        sql.append("   sum(case abs.id when :ACTI  then 1 else 0 end)   ");
        sql.append(" )   ");
        sql.append("  from ").append(AnexoBoletin.class.getName()).append(" as ab ");
        sql.append(" inner join  ab.anexoSuperior abs ");
        sql.append(" left join  ab.departamentoAcademico da ");
        sql.append(" left join  ab.carrera ca ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("INGRE", GrupoAnexoEnum.INGRESANTE.getValue());
        query.setString("DPTO", GrupoAnexoEnum.DPTO.getValue());
        query.setString("ACTI", GrupoAnexoEnum.ACTIVIDADES.getValue());
        query.setString("POST", GrupoAnexoEnum.POSTGRADO.getValue());

        return (CarreraResumen) query.uniqueResult();
    }

}
