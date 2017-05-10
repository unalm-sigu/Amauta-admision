package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.zelper.enums.TipoPlanCalificacionEnum;

@Repository
public class PlanCalificacionDAOH extends AbstractDAO<PlanCalificacion> implements PlanCalificacionDAO {

    public PlanCalificacionDAOH() {
        super();
        setClazz(PlanCalificacion.class);
    }

    @Override
    public List<PlanCalificacion> allByDynatable(DynatableFilter filter, DepartamentoAcademico dpto) {
        filter.setFields(Arrays.asList("pc.formula", "pc.codigo"));
        filter.setAlias("pc");
        filter.setParents("departamentoAcademico da", "left sistemaNotas sn");//, "left curso cur"
        filter.filterFix("da.id", dpto.getId());

        filter.filterInFix("pc.tipo", Arrays.asList(TipoPlanCalificacionEnum.PLANT.name(), "0"));

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }
        Map filterFixIn = filter.getFiltersInFixed();
        for (Object key : filterFixIn.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
        }

        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());
        sqlUtil.orderBy("pc.id desc");
        List<PlanCalificacion> listaRestultado = this.all(sqlUtil);
        for (PlanCalificacion plan : listaRestultado) {
            if (plan.isTipoCicloNivelacion()) {
                if (plan.getCurso() != null) {
                    for (Curso curso : plan.getCurso()) {
                        curso.getId();
                    }
                }
            }
            if (plan.isTipoCicloNivelacion()) {
                if (plan.getCursosPlanRegular() != null) {
                    for (Curso curso : plan.getCursosPlanRegular()) {
                        curso.getId();
                    }
                }
            }
        }
        return listaRestultado;
    }

    @Override
    public PlanCalificacion find(Long idPlanCalificacion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pc");
        sqlUtil.parents("sistemaNotas sn", "departamentoAcademico da", "left evaluacionPlan ep");
        sqlUtil.filter("pc.id", idPlanCalificacion);

        PlanCalificacion result = find(sqlUtil);
        if (result.getEvaluacionPlan() != null) {
            for (EvaluacionPlan evaPlan : result.getEvaluacionPlan()) {
                evaPlan.getTipoEvaluacion();
            }
        }
        return result;
    }

    @Override
    public Long maxNumeroCorrelativoPlanCalifica(Long idDepartamentoAcademico) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append("Select max(pc.numero) from PlanCalificacion pc ");
        strQuery.append(" inner join  pc.departamentoAcademico da ");
        strQuery.append(" where da.id=:prm_departamento_academico ");
        Query query = getCurrentSession().createQuery(strQuery.toString());
        query.setParameter("prm_departamento_academico", idDepartamentoAcademico);
        Long result = (Long) query.uniqueResult();
        if (result == null) {
            result = 0L;
        }
        return result;
    }
}
