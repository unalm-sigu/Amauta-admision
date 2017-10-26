package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.model.academico.Docente;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class DocenteDAOH extends AbstractDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }

    @Override
    public Docente find(Long idDocente) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per")
                .filter("doc.id", idDocente);
        return find(sqlUtil);
    }

    @Override
    public Docente findPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("per.id", persona);
        return find(sqlUtil);
    }

    @Override
    public Docente findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("left persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("doc.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<Docente> allByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per")
                .filter("per.id", persona);
        return all(sqlUtil);
    }

    @Override
    public List<Docente> allActivos(ModalidadEstudio modalidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per", "modalidadEstudio me")
                .filter("doc.estado", EstadoEnum.ACT.name())
                .filter("me.id", modalidad);
        return all(sqlUtil);
    }

    @Override
    public List<Docente> allByFilter(DynatableFilter filter) {

        {

            StringBuilder sql = new StringBuilder();
            sql.append("    select count(doc) ");
            sql.append("    from ").append(Docente.class.getName()).append("  doc ");
            sql.append("    inner join doc.departamentoAcademico da ");
            sql.append("    inner join doc.persona p ");
            sql.append("    inner join da.facultad fa ");
            sql.append("    inner join p.tipoDocumento td ");

            Query query = getCurrentSession().createQuery(sql.toString());

            Long total = (Long) query.uniqueResult();
            filter.setTotal(total.intValue());

        }

        {

            StringBuilder sql = new StringBuilder();
            sql.append("    select count(doc) ");
            sql.append("    from ").append(Docente.class.getName()).append("  doc ");
            sql.append("    inner join  doc.persona p ");
            sql.append("    inner join doc.departamentoAcademico da ");
            sql.append("    inner join da.facultad fa ");
            sql.append("    inner join  p.tipoDocumento td ");
            sql.append("    where 1=1");

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
            sql.append("    select doc ");
            sql.append("    from ").append(Docente.class.getName()).append("  doc ");
            sql.append("    inner join fetch doc.persona p ");
            sql.append("    inner join fetch doc.departamentoAcademico da ");
            sql.append("    inner join fetch da.facultad fa ");
            sql.append("    inner join fetch p.tipoDocumento td ");
            sql.append("    where 1=1");

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

            sql.append("order by doc.id desc");

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
    public Docente findDocente(Docente docente) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("doc.id", docente);
        return find(sqlUtil);
    }

    @Override
    public Docente findDocenteByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("per.id", persona);
        return find(sqlUtil);
    }
}
