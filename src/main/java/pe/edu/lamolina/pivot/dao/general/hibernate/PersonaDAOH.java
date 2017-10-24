package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.model.general.Persona;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;

@Repository
public class PersonaDAOH extends AbstractDAO<Persona> implements PersonaDAO {

    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }

    @Override
    public List<Persona> allByNombre(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Persona.class.getName()).append(" as per ");
        sql.append(" where concat( coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',per.nombres) like :BUSQUEDA or ");
        sql.append("       concat( per.nombres,' ',coalesce(per.paterno,''),' ',coalesce(per.materno,'')) like :BUSQUEDA or ");
        sql.append("       per.numeroDocIdentidad like :BUSQUEDA ");
        sql.append(" order by per.paterno, per.materno, per.nombres ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("BUSQUEDA", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Persona> allByFilter(DynatableFilter filter) {

        {

            StringBuilder sql = new StringBuilder();
            sql.append("select count(p) ");
            sql.append("  from ").append(Persona.class.getName()).append("  p ");
            sql.append(" inner join p.tipoDocumento td ");

            Query query = getCurrentSession().createQuery(sql.toString());

            Long total = (Long) query.uniqueResult();
            filter.setTotal(total.intValue());

        }

        {

            StringBuilder sql = new StringBuilder();
            sql.append("select count(p) ");
            sql.append("  from ").append(Persona.class.getName()).append("  p ");
            sql.append(" inner join p.tipoDocumento td ");
            sql.append(" where 1=1");

            if (!filter.getSearchValue().equalsIgnoreCase("")) {
                sql.append(" and ( ");
                sql.append("    concat( coalesce(p.paterno,''),' ',coalesce(p.materno,''),' ',p.nombres) like :SEARCH ");
                sql.append("    or concat( p.nombres,' ',coalesce(p.paterno,''),' ',coalesce(p.materno,'')) like :SEARCH ");
                sql.append("    or td.simbolo like :SEARCH ");
                sql.append("    or p.numeroDocIdentidad like :SEARCH ");
                sql.append("    or p.telefono like :SEARCH ");
                sql.append("    or p.celular like :SEARCH ");
                sql.append("    or p.emailCompania like :SEARCH ");
                sql.append(" ) ");
            }

            Query query = getCurrentSession().createQuery(sql.toString());

            if (!filter.getSearchValue().equalsIgnoreCase("")) {
                query.setString("SEARCH", "%" + filter.getSearchValue() + "%");
            }

            Long total = (Long) query.uniqueResult();
            filter.setFiltered(total.intValue());

        }

        {

            StringBuilder sql = new StringBuilder();
            sql.append(" from ").append(Persona.class.getName()).append("  p ");
            sql.append(" inner join fetch p.tipoDocumento td ");
            sql.append(" where 1=1");

            if (!filter.getSearchValue().equalsIgnoreCase("")) {
                sql.append(" and ( ");
                sql.append("    concat( coalesce(p.paterno,''),' ',coalesce(p.materno,''),' ',p.nombres) like :SEARCH ");
                sql.append("    or concat( p.nombres,' ',coalesce(p.paterno,''),' ',coalesce(p.materno,'')) like :SEARCH ");
                sql.append("    or td.simbolo like :SEARCH ");
                sql.append("    or p.numeroDocIdentidad like :SEARCH ");
                sql.append("    or p.telefono like :SEARCH ");
                sql.append("    or p.celular like :SEARCH ");
                sql.append("    or p.emailCompania like :SEARCH ");
                sql.append(" ) ");
            }

            sql.append("order by p.id desc");

            Query query = getCurrentSession().createQuery(sql.toString());

            if (!filter.getSearchValue().equalsIgnoreCase("")) {
                query.setString("SEARCH", "%" + filter.getSearchValue() + "%");
            }

            query.setMaxResults(filter.getPerPage());
            query.setFirstResult((filter.getPage() - 1) * filter.getPerPage());

            return query.list();
        }
    }

    @Override
    public Persona findByDocIdentidad(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pe")
                .parents("tipoDocumento di")
                .filter("pe.numeroDocIdentidad", numeroDocIdentidad)
                .filter("di.id", tipoDocumento.getId());
        return this.find(sqlUtil);
    }

    @Override
    public List<Persona> allByEmail(String email) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("p")
                .parents("tipoDocumento di")
                .filter("p.email", email);
        return this.all(sqlUtil);
    }

    @Override
    public List<Persona> allByEmailWithoutPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("p")
                .parents("tipoDocumento di")
                .filter("p.email", persona.getEmail())
                .filter("p.id <>", persona.getId());
        return this.all(sqlUtil);
    }

    @Override
    public List<Persona> allByEmailEmpresa(String email) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("p")
                .parents("tipoDocumento di")
                .filter("p.emailCompania", email);
        return this.all(sqlUtil);
    }

    @Override
    public List<Persona> allByEmailEmpresaWithoutPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("p")
                .parents("tipoDocumento di")
                .filter("p.emailCompania", persona.getEmailCompania())
                .filter("p.id <>", persona.getId());
        return this.all(sqlUtil);
    }

    @Override
    public List<Persona> allByApellidosNombres(Persona persona) {
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Persona.class.getName()).append(" as per ");
        sql.append("  left join fetch per.tipoDocumento ");
        sql.append(" where per.paterno like :PATERNO ");
        sql.append("   and per.materno like :MATERNO ");
        sql.append("   and per.nombres like :NOMBRES ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("PATERNO", persona.getPaterno());
        query.setString("MATERNO", persona.getMaterno());
        query.setString("NOMBRES", persona.getNombres());

        return query.list();
    }

    @Override
    public List<Persona> allByEmailCompania(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", email);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Persona> allByEmailCompaniaWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", persona.getEmailCompania())
                .filter("per.id", "<>", persona.getId());

        return sql.all(getCurrentSession());
    }

}
