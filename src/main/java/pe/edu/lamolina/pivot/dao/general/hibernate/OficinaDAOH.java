package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.model.general.Oficina;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class OficinaDAOH extends AbstractEasyDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }

    @Override
    public Oficina find(long id) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .leftJoin("personaJefe pj", "jefeEncargado", "cargoJefe", "oficinaSuperior")
                .filter("ofi.id", id);
        return find(sql);
    }

    @Override
    public List<Oficina> allByJefe(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("personaJefe pj")
                .filter("pj.id", persona);
        return all(sql);
    }

    @Override
    public List<Oficina> allByFilter(DynatableFilter filter, Compania compania) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .leftJoin("oficinaSuperior sup", "personaJefe pj", "jefeEncargado pje", "cargoJefe ca")
                .filter("cia.id", compania)
                .searchFields("ofi.codigo", "ofi.nombre", "ofi.tipoOficina", "ca.nombre")
                .searchComplexField("concat(coalesce(pj.paterno,''),' ',coalesce(pj.materno,''),' ',coalesce(pj.nombres,''))")
                .searchComplexField("concat(coalesce(pj.nombres,''),' ',coalesce(pj.paterno,''),' ',coalesce(pj.materno,''))")
                .searchComplexField("concat(coalesce(pje.paterno,''),' ',coalesce(pje.materno,''),' ',coalesce(pje.nombres,''))")
                .searchComplexField("concat(coalesce(pje.nombres,''),' ',coalesce(pje.paterno,''),' ',coalesce(pje.materno,''))")
                .orderBy("ofi.id DESC");

        return all(sql);
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

    @Override
    public List<Oficina> allOficinasByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .filter("ofi.nombre", "like", nombre);
        return all(sql);
    }
}
