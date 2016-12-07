package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

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
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("es");
        sqlUtil.parents("planCalificacion pc", "grupoSeccion gs");
        sqlUtil.filter("es.id", id);
        return find(sqlUtil);
    }
}
