package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;

@Repository
public class CursoDAOH extends AbstractDAO<Curso> implements CursoDAO {

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public List<Curso> allAutocomplete(String nombre, Long idDepartamentoAca) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" left join fetch cur.departamentoAcademico da ");
        sql.append(" left join fetch cur.planCalificacion se ");
        sql.append(" where 1=1 ");
        sql.append("  and    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, Long idPlanCalificacion) {
        List<String> fieldsFiltro = Arrays.asList(
                "c.nombre", "c.codigo", "c.fechaPlanCalificacion");

        filter.setFields(fieldsFiltro);

        filter.setAlias("c");
        filter.setParents("planCalificacion pc");

        filter.filterFix("pc.id", idPlanCalificacion);

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filterFixIn = filter.getFiltersInFixed();
        if (filterFixIn != null) {
            for (Object key : filterFixIn.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
            }
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

}
