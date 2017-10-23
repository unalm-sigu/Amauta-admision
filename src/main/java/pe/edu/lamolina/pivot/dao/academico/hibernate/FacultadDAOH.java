package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class FacultadDAOH extends AbstractDAO<Facultad> implements FacultadDAO {

    public FacultadDAOH() {
        super();
        setClazz(Facultad.class);
    }

    @Override
    public List<Facultad> allDynatable(DynatableFilter filter) {

        StringBuilder sql;
        Query query;

        String search = filter.getSearchValue();

        if (!StringUtils.isEmpty(search)) {
            search = "%" + search.replaceAll(" ", "%") + "%";
        }

        {
            sql = new StringBuilder();
            sql.append("  select count( distinct fa ) ");
            sql.append("  from ").append(Facultad.class.getName()).append(" as fa ");
            sql.append("  where 1 = 1 ");

            query = getCurrentSession().createQuery(sql.toString());
            filter.setTotal(((Long) query.uniqueResult()).intValue());
        }

        {
            sql = new StringBuilder();
            sql.append("  select count( distinct fa ) ");
            sql.append("  from ").append(Facultad.class.getName()).append(" as fa ");
            sql.append("  where 1 = 1 ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    fa.nombre like :SEARCH ");
                sql.append("    or fa.codigo like :SEARCH ");
                sql.append("    or fa.estado like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());
            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }
            filter.setFiltered(((Long) query.uniqueResult()).intValue());
        }

        {
            sql = new StringBuilder();
            sql.append("  select distinct fa ");
            sql.append("  from ").append(Facultad.class.getName()).append(" as fa ");
            sql.append("  where 1 = 1 ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    fa.nombre like :SEARCH ");
                sql.append("    or fa.codigo like :SEARCH ");
                sql.append("    or fa.estado like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());
            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }
            query.setFirstResult((filter.getPage() - 1) * filter.getPerPage());
            query.setMaxResults(filter.getPerPage());

            return query.list();
        }

    }

    @Override
    public List<Facultad> allByCompania(Compania compania) {
        SqlUtil sqlUtil = new SqlUtil("fa")
                .parents("compania co")
                .filter("co.id", compania);
        return all(sqlUtil);
    }

    @Override
    public List<Facultad> allActivos() {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .join("compania")
                .filter("estado", EstadoEnum.ACT.name());
        return sql.all(getCurrentSession());
    }
}
