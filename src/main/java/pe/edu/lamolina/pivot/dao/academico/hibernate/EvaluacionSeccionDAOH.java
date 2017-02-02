package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;

@Repository
public class EvaluacionSeccionDAOH extends AbstractDAO<EvaluacionSeccion> implements EvaluacionSeccionDAO {

    public EvaluacionSeccionDAOH() {
        super();
        setClazz(EvaluacionSeccion.class);
    }

    @Override
    public EvaluacionSeccion findByPlanCalGrupoSec(Long idPlanCalificacion, Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("es");
        sqlUtil.parents("planCalificacion pc", "grupoSeccion gs", "sistemaNotas sn");

        if (idPlanCalificacion != null) {
            sqlUtil.filter("pc.id", idPlanCalificacion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }

        return this.find(sqlUtil);
    }

    @Override
    public EvaluacionSeccion find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("es")
                .parents("planCalificacion pc", "grupoSeccion gs")
                .filter("es.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<EvaluacionSeccion> allByPlan(PlanCalificacion plan) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("es")
                .parents("planCalificacion pc", "grupoSeccion gs")
                .filter("pc.id", plan);

        return all(sqlUtil);
    }

    @Override
    public List<EvaluacionSeccion> allByGrupoSeccion(GrupoSeccion gpoSecc) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("es")
                .parents("planCalificacion pc", "grupoSeccion gs")
                .filter("gs.id", gpoSecc);

        return all(sqlUtil);
    }

}
