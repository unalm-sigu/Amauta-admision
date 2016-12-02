package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;

@Repository
public class EvaluacionExpandidaDAOH extends AbstractDAO<EvaluacionExpandida> implements EvaluacionExpandidaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionExpandidaDAOH() {
        super();
        setClazz(EvaluacionExpandida.class);
    }

    public EvaluacionExpandida find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("tipoEvaluacion te");
        sqlUtil.filter("eva.id", id);
        EvaluacionExpandida evaluacion = this.find(sqlUtil);
        if (evaluacion.getEvaluaciones() != null) {
            for (EvaluacionExpandida eva : evaluacion.getEvaluaciones()) {
                eva.getId();
                eva.getTipoEvaluacion().getId();
            }
        }
        return evaluacion;
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion) {
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
        sqlUtil.filterIsNull("esup.id");

        List<EvaluacionExpandida> lstEvaluaciones = this.all(sqlUtil);
        if (!lstEvaluaciones.isEmpty()) {
            for (EvaluacionExpandida objEvaluacion : lstEvaluaciones) {
                if (objEvaluacion.getEvaluaciones() != null && !objEvaluacion.getEvaluaciones().isEmpty()) {
                    for (EvaluacionExpandida eva : objEvaluacion.getEvaluaciones()) {
                        eva.getId();
                        eva.getTipoEvaluacion().getId();
                    }
                }
            }
        }
        return lstEvaluaciones;
    }
}
