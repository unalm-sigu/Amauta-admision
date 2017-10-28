package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.model.general.Oficina;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.TipoOficinaEnum;

@Repository
public class OficinaDAOH extends AbstractDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }

    @Override
    public List<Oficina> allByJefe(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ofi")
                .parents("personaJefe pj")
                .filter("pj.id", persona);
        return all(sqlUtil);
    }

    @Override
    public List<Oficina> allByFilter(DynatableFilter filter, Compania compania) {

        StringBuilder sql;
        Query query;

        String search = filter.getSearchValue();

        if (!StringUtils.isEmpty(search)) {
            search = "%" + search.replaceAll(" ", "%") + "%";
        }

        {
            sql = new StringBuilder();
            sql.append("  select count( distinct ofi ) ");
            sql.append("  from ").append(Oficina.class.getName()).append(" as ofi ");
            sql.append("  inner join ofi.compania cia ");
            sql.append("  where 1 = 1 ");
            sql.append("  and cia.id =:COMPANIA ");

            query = getCurrentSession().createQuery(sql.toString());
            query.setLong("COMPANIA", compania.getId());

            filter.setTotal(((Long) query.uniqueResult()).intValue());

        }

        {

            sql = new StringBuilder();
            sql.append("  select count( distinct ofi ) ");
            sql.append("  from ").append(Oficina.class.getName()).append(" as ofi ");
            sql.append("  inner join ofi.compania cia ");
            sql.append("  where 1 = 1 ");
            sql.append("  and cia.id =:COMPANIA ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    ofi.nombre like :SEARCH ");
                sql.append("    or ofi.codigo like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());

            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }

            query.setLong("COMPANIA", compania.getId());
            filter.setFiltered(((Long) query.uniqueResult()).intValue());

        }

        {

            sql = new StringBuilder();
            sql.append("  select distinct ofi ");
            sql.append("  from ").append(Oficina.class.getName()).append(" as ofi ");
            sql.append("  inner join ofi.compania cia ");
            sql.append("  where 1 = 1 ");
            sql.append("  and cia.id =:COMPANIA ");

            if (!StringUtils.isEmpty(search)) {
                sql.append("    and  ( ");
                sql.append("    ofi.nombre like :SEARCH ");
                sql.append("    or ofi.codigo like :SEARCH ");
                sql.append("    )    ");
            }

            query = getCurrentSession().createQuery(sql.toString());
            if (!StringUtils.isEmpty(search)) {
                query.setString("SEARCH", search);
            }

            query.setLong("COMPANIA", compania.getId());
            query.setFirstResult((filter.getPage() - 1) * filter.getPerPage());
            query.setMaxResults(filter.getPerPage());

            return query.list();

        }
    }

    @Override
    public List<Oficina> allUnidadSuperior(String nombre, Compania compania) {

        Criteria criteria = getCurrentSession().createCriteria(Oficina.class, "ofi");
        criteria.add(Restrictions.eq("compania", compania));

        if (!"".equalsIgnoreCase(nombre)) {
            String searchValue = nombre.trim().replaceAll("\\s+", "%");
            Disjunction criteriaConjunction = Restrictions.disjunction();
            criteriaConjunction.add(Restrictions.like("ofi.codigo", searchValue, MatchMode.ANYWHERE));
            criteriaConjunction.add(Restrictions.like("ofi.nombre", searchValue, MatchMode.ANYWHERE));
            criteria.add(criteriaConjunction);
        }

        criteria.addOrder(Order.asc("ofi.nombre"));
        criteria.setMaxResults(10);
        return criteria.list();
    }
}
