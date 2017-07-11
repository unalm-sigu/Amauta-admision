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
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class EvaluacionExpandidaDAOH extends AbstractDAO<EvaluacionExpandida> implements EvaluacionExpandidaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionExpandidaDAOH() {
        super();
        setClazz(EvaluacionExpandida.class);
    }

    @Override
    public EvaluacionExpandida find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva")
                .parents("tipoEvaluacion te", "left evaluacionSuperior es", "evaluacionSeccion sc", "evaluaciones evals")
                .parents("_sc.grupoSeccion gs")
                .filter("eva.id", id);

        EvaluacionExpandida evaluacion = this.find(sqlUtil);
        System.out.println(evaluacion);

        sqlUtil = SqlUtil.creaSqlUtil("eva")
                .parents("tipoEvaluacion te", "evaluacionSuperior es", "evaluacionSeccion sc")
                .parents("_sc.grupoSeccion gs")
                .filter("es.id", id);

        List<EvaluacionExpandida> evaluacionesHija = this.all(sqlUtil);
        evaluacion.setEvaluacionesExpandidas(evaluacionesHija);

        for (EvaluacionExpandida evaluacionExpandida : evaluacion.getEvaluacionesExpandidas()) {
            for (EvaluacionExpandida evaluacionExpandida2 : evaluacionExpandida.getEvaluacionesExpandidas()) {
                evaluacionExpandida2.getId();
            }
        }

        return evaluacion;
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup) {
        return this.allByFilter(idEvaluacionSeccion, idGrupoSeccion, idEvaluacionExpSup, EstadoEnum.ACT);
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup, EstadoEnum estadoEnum) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva")
                .parents("evaluacionSeccion es", "tipoEvaluacion te")
                .parents("_es.grupoSeccion gs")
                .parents("left evaluacionSuperior esup")
                .orderBy("te.orden", "eva.numero");
        if (idEvaluacionExpSup == null) {
            sqlUtil.filterIsNull("esup.id");
        }
        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idEvaluacionExpSup != null) {
            sqlUtil.filter("esup.id", idEvaluacionExpSup);
        }

        if (estadoEnum != null) {
            sqlUtil.filter("eva.estado", estadoEnum.name());
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
