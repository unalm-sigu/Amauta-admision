package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class PlanCalificacionCursoDAOH extends AbstractDAO<PlanCalificacionCurso> implements PlanCalificacionCursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlanCalificacionCursoDAOH() {
        super();
        setClazz(PlanCalificacionCurso.class);
    }

    @Override
    public PlanCalificacionCurso findByFilter(PlanCalificacion planCalificacion, Curso curso, EstadoEnum estadoEnum) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pc");
        sqlUtil.parents("planCalificacion pln", "curso cur");
        if (planCalificacion != null) {
            sqlUtil.filter("pln.id", planCalificacion.getId());
        }
        if (curso != null) {
            sqlUtil.filter("cur.id", curso.getId());
        }
        if (estadoEnum != null) {
            sqlUtil.filter("pc.estado", estadoEnum.name());
        }
        return find(sqlUtil);
    }

    @Override
    public List<PlanCalificacionCurso> allByFilter(PlanCalificacion planCalificacion, Curso curso, EstadoEnum estadoEnum) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pc");
        sqlUtil.parents("planCalificacion pln", "curso cur");
        if (planCalificacion != null) {
            sqlUtil.filter("pln.id", planCalificacion.getId());
        }
        if (curso != null) {
            sqlUtil.filter("cur.id", curso.getId());
        }
        if (estadoEnum != null) {
            sqlUtil.filter("pc.estado", estadoEnum.name());
        }
        return all(sqlUtil);
    }

    @Override
    public List<PlanCalificacionCurso> allByFilterDyna(DynatableFilter filter, PlanCalificacion planCalificacion, EstadoEnum estadoPlanCurdo) {
        List<String> fieldsFiltro = Arrays.asList("cur.nombre", "cur.codigo", "cur.fechaPlanCalificacion");

        filter.setFields(fieldsFiltro);

        filter.setAlias("plncur");
        filter.setParents("planCalificacion pc", "curso cur", "_pc.departamentoAcademico da");

        if (planCalificacion != null) {
            filter.filterFix("pc.id", planCalificacion.getId());
        }
        /*
        if (departamentoAcademico != null) {
            filter.filterFix("da.id", departamentoAcademico.getId());
        }
         */
        if (estadoPlanCurdo != null) {
            filter.filterFix("plncur.estado", estadoPlanCurdo.name());
        }
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
