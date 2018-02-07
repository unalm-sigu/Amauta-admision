package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.PlanCalificacion;

@Repository
public class PlanCalificacionDAOH extends AbstractEasyDAO<PlanCalificacion> implements PlanCalificacionDAO {

    public PlanCalificacionDAOH() {
        super();
        setClazz(PlanCalificacion.class);
    }

    @Override
    public List<PlanCalificacion> allByDynatable(DynatableFilter filter, DepartamentoAcademico dpto) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCalificacion.class, "pc")
                .join("departamentoAcademico da")
                .left("sistemaNotas sn")
                .filter("da.id", dpto)
                .searchFields("pc.formula", "pc.codigo")
                .orderBy("pc.id desc");

        return all(sql);
    }

    @Override
    public PlanCalificacion find(Long idPlanCalificacion) {
        Octavia sql = Octavia.query()
                .from(PlanCalificacion.class, "pc")
                .join("sistemaNotas sn", "departamentoAcademico da")
                .leftJoin("evaluacionPlan ep")
                .filter("pc.id", idPlanCalificacion);

        return find(sql);
    }

    @Override
    public Long maxNumeroCorrelativoPlanCalifica(Long idDepartamentoAcademico) {
        StringBuilder sql = new StringBuilder();
        sql.append("Select max(pc.numero) from PlanCalificacion pc ");
        sql.append(" inner join  pc.departamentoAcademico da ");
        sql.append(" where da.id=:prm_departamento_academico ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("prm_departamento_academico", idDepartamentoAcademico);

        Long result = (Long) query.uniqueResult();
        if (result == null) {
            result = 0L;
        }
        return result;
    }

    @Override
    public List<PlanCalificacion> all(List<Long> ids) {
        Octavia sql = Octavia.query()
                .from(PlanCalificacion.class, "pc")
                .leftJoin("sistemaNotas sn")
                .in("pc.id", ids);

        return all(sql);
    }

}