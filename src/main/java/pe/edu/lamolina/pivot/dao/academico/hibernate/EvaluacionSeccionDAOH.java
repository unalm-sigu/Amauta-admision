package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;

@Repository
public class EvaluacionSeccionDAOH extends AbstractEasyDAO<EvaluacionSeccion> implements EvaluacionSeccionDAO {

    public EvaluacionSeccionDAOH() {
        super();
        setClazz(EvaluacionSeccion.class);
    }

    @Override
    public EvaluacionSeccion findByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion, EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        Octavia sql = Octavia.query()
                .from(EvaluacionSeccion.class, "es")
                .join("grupoSeccion gs")
                .leftJoin("planCalificacion pc", "sistemaNotas sn");

        if (idPlanCalificacion != null) {
            sql.filter("pc.id", idPlanCalificacion);
        }
        if (idGrupoSeccion != null) {
            sql.filter("gs.id", idGrupoSeccion);
        }
        if (estadoPlanCalificaEnum != null) {
            sql.filter("es.estado", estadoPlanCalificaEnum);
        }

        return find(sql);
    }

    @Override
    public EvaluacionSeccion find(Long id) {
        Octavia sql = Octavia.query()
                .from(EvaluacionSeccion.class, "es")
                .join("planCalificacion pc", "grupoSeccion gs", "gs.curso")
                .filter("es.id", id);

        return find(sql);
    }

    @Override
    public List<EvaluacionSeccion> allByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(EvaluacionSeccion.class, "es")
                .join("planCalificacion pc", "grupoSeccion gs", "gs.curso")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public List<EvaluacionSeccion> allByGrupoSeccion(GrupoSeccion gpoSecc) {
        Octavia sql = Octavia.query()
                .from(EvaluacionSeccion.class, "es")
                .join("grupoSeccion gs", "gs.curso")
                .leftJoin("planCalificacion pc")
                .filter("gs.id", gpoSecc);

        return all(sql);
    }

}
