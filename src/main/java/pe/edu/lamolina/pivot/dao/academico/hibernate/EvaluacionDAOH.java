package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class EvaluacionDAOH extends AbstractDAO<Evaluacion> implements EvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionDAOH() {
        super();
        setClazz(Evaluacion.class);
    }

    public Evaluacion find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("tipoEvaluacion te");
        sqlUtil.filter("eva.id", id);
        Evaluacion evaluacion = this.find(sqlUtil);
        if (evaluacion.getEvaluaciones() != null) {
            for (Evaluacion eva : evaluacion.getEvaluaciones()) {
                eva.getId();
                eva.getTipoEvaluacion().getId();
            }
        }
        return evaluacion;
    }

    @Override
    public List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("evaluacionSeccion es", "tipoEvaluacion te", "left seccionResponsable sr");
        sqlUtil.parents("_es.grupoSeccion gs", "left seccionResponsable");
        sqlUtil.parents("left evaluacionSuperior esup");
        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sqlUtil.filter("sr.id", idGrupoSeccion);
        }
        sqlUtil.filterIsNull("esup.id");

        List<Evaluacion> lstEvaluaciones = this.all(sqlUtil);
        if (!lstEvaluaciones.isEmpty()) {
            for (Evaluacion objEvaluacion : lstEvaluaciones) {
                for (Evaluacion eva : objEvaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                }
            }
        }
        return lstEvaluaciones;
    }
}
