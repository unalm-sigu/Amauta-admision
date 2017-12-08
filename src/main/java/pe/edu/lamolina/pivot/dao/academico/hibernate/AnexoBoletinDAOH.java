package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.controller.academico.anexoboletin.AnexoResumen;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;

@Repository
public class AnexoBoletinDAOH extends AbstractDAO<AnexoBoletin> implements AnexoBoletinDAO {

    public AnexoBoletinDAOH() {
        super();
        setClazz(AnexoBoletin.class);
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
    public List<AnexoBoletin> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior ass")
                .leftJoin("departamentoAcademico da", "carrera ca")
                .searchFields("ab.nombre", "da.nombre")
                .orderBy("ab.id desc");

        sql.beginRelativeFilters();
        this.setGrupoAnexo(filter, sql);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "carrera ca", "anexoSuperior abs")
                .isNull("abs.id");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<AnexoBoletin> allAnexosHijos() {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .leftJoin("departamentoAcademico da", "carrera ca", "anexoSuperior abs")
                .isNotNull("abs.id");
        return sql.all(getCurrentSession());
    }

    @Override
    public AnexoBoletin find(Long id) {
        Octavia sql = Octavia.query()
                .from(AnexoBoletin.class, "ab")
                .join("anexoSuperior abs")
                .leftJoin("departamentoAcademico da", "carrera ca")
                .filter("ab.id", id);
        return (AnexoBoletin) sql.find(getCurrentSession());
    }

    @Override
    public AnexoResumen resumen() {
        StringBuilder sql = new StringBuilder();
        sql.append("select new ").append(AnexoResumen.class.getName());
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

        return (AnexoResumen) query.uniqueResult();

    }

}
