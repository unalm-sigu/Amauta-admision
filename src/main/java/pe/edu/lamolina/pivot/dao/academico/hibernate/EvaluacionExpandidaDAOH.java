package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;

@Repository
public class EvaluacionExpandidaDAOH extends AbstractDAO<EvaluacionExpandida> implements EvaluacionExpandidaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionExpandidaDAOH() {
        super();
        setClazz(EvaluacionExpandida.class);
    }

    public EvaluacionExpandida find(Long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("tipoEvaluacion te", "left evaluacionSuperior es", "evaluacionSeccion sc");
        sqlUtil.parents("_sc.grupoSeccion gs");
        sqlUtil.filter("eva.id", id);
        EvaluacionExpandida evaluacion = this.find(sqlUtil);
        if (evaluacion.getEvaluacionesExpandidas() != null) {
            for (EvaluacionExpandida eva : evaluacion.getEvaluacionesExpandidas()) {
                eva.getId();
                eva.getTipoEvaluacion().getId();
            }
        }
        return evaluacion;
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva")
                .parents("evaluacionSeccion es", "tipoEvaluacion te")
                .parents("_es.grupoSeccion gs")
                .parents("left evaluacionSuperior esup")
                .filterIsNull("esup.id");

        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }

        List<EvaluacionExpandida> lstEvaluaciones = this.all(sqlUtil);
        for (EvaluacionExpandida objEvaluacion : lstEvaluaciones) {
            if (objEvaluacion.getEvaluacionesExpandidas() != null) {
                for (EvaluacionExpandida eva : objEvaluacion.getEvaluacionesExpandidas()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                }
            }
        }
        return lstEvaluaciones;
    }

    @Override
    public void deleteByEvaluacionParent(Long idEvaluacionParent) {
        String strQuery = "delete from EvaluacionExpandida eva where eva.evaluacionSuperior.id=:prm_evaluacion_exp and eva.indNotasIngresadas=0";

        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("prm_evaluacion_exp", idEvaluacionParent);
        query.executeUpdate();
    }

    @Override
    public List<EvaluacionExpandida> allByEvaluacionSeccion(EvaluacionSeccion evalSecc) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev");
        sqlUtil.parents("evaluacionSeccion es");
        sqlUtil.filter("es.id", evalSecc);

        return all(sqlUtil);
    }

}
