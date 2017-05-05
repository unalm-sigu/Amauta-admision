package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCicloEnum;

@Repository
public class CursoDAOH extends AbstractDAO<Curso> implements CursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public Curso find(Long idCurso) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("left planCalificacion pc", "left departamentoAcademico da", "left _da.facultad")
                .filter("cur.id", idCurso);
        return find(sqlUtil);
    }

    @Override
    public List<Curso> allForSistemaCalificacion(String nombre, Long idDepartamentoAca, PlanCalificacion planCalificacion, Long idCiclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" left join fetch cur.departamentoAcademico da ");
        sql.append(" left join fetch cur.planCalificacion pc ");
        sql.append(" where 1=1 ");
        if (planCalificacion.isTipoCicloNivelacion()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else if (planCalificacion.isTipoCicloRegular()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else {
            throw new PhobosException("El plan calificacion no tiene tipo de ciclo");
        }
        sql.append("  and    cur.id in ( select cu.id ");
        sql.append("                       from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("                      inner join gs.curso cu ");
        sql.append("                      inner join gs.cicloAcademico ca ");
        sql.append("                      where ca.id = :CICLO ) ");
        sql.append("  and    da.id = :DEP_ACA ");
        sql.append("  and    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("PLAN_CAL", planCalificacion.getId());
        query.setParameter("DEP_ACA", idDepartamentoAca);
        query.setParameter("CICLO", idCiclo);
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, PlanCalificacion planCalificacion, Long idDepartamentoAcademico) {
        List<String> fieldsFiltro = Arrays.asList(
                "c.nombre", "c.codigo", "c.fechaPlanCalificacion");

        filter.setFields(fieldsFiltro);

        filter.setAlias("c");
        filter.setParents("left planCalificacion pc", "left planCalificacionRegular pcr", "departamentoAcademico da");

        if (planCalificacion.isTipoCicloNivelacion()) {
            logger.debug("Tipo Ciclo Nivelacion");
            filter.filterFix("pc.id", planCalificacion.getId());
        } else if (planCalificacion.isTipoCicloRegular()) {
            logger.debug("Tipo Ciclo Regular");
            filter.filterFix("pcr.id", planCalificacion.getId());
        }
        filter.filterFix("da.id", idDepartamentoAcademico);

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

    @Override
    public List<Curso> allByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("planCalificacion pc")
                .filter("pc.id", plan);
        return all(sqlUtil);
    }

    @Override
    public List<Curso> allByPlanRegular(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("planCalificacionRegular pc")
                .filter("pc.id", plan);
        return all(sqlUtil);
    }

    @Override
    public List<Curso> allActiveByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("left planCalificacion pc", "left planCalificacionRegular pcr")
                .filter("pc.estado", EstadoEnum.ACT.name());
        if (plan.isTipoCicloNivelacion()) {
            sqlUtil.filter("pc.id", plan);
        } else if (plan.isTipoCicloRegular()) {
            sqlUtil.filter("pcr.id", plan);
        } else {
            throw new PhobosException("PLan calificacion de tipo de ciclo");
        }
        return all(sqlUtil);
    }

    @Override
    public Curso findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cur")
                .parents("left planCalificacion pc", "left departamentoAcademico da", "left _da.facultad")
                .filter("cur.codigo", codigo);
        return find(sqlUtil);
    }

}
