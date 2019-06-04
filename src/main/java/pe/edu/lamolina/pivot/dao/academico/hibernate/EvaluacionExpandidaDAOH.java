package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;

@Repository
public class EvaluacionExpandidaDAOH extends AbstractEasyDAO<EvaluacionExpandida> implements EvaluacionExpandidaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionExpandidaDAOH() {
        super();
        setClazz(EvaluacionExpandida.class);
    }

    @Override
    public EvaluacionExpandida find(long id) {
        Octavia sql = Octavia.query()
                .from(EvaluacionExpandida.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSeccion es", "es.grupoSeccion gs")
                .leftJoin("evaluacionSuperior esup")
                .filter("eva.id", id);
        EvaluacionExpandida evaluacionExpandida = find(sql);

        List<EvaluacionExpandida> evalExpanHijas = allHijas(evaluacionExpandida);

        for (EvaluacionExpandida evaExp : evalExpanHijas) {
            evaExp.setEvaluacionesExpandidas(allHijas(evaExp));
        }

        evaluacionExpandida.setEvaluacionesExpandidas(evalExpanHijas);
        return evaluacionExpandida;
    }

    private List<EvaluacionExpandida> allHijas(EvaluacionExpandida evalExpan) {
        Octavia sql = Octavia.query()
                .from(EvaluacionExpandida.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSuperior esup", "evaluacionSeccion es", "es.grupoSeccion gs")
                .filter("esup.id", evalExpan.getId());
        return all(sql);
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup) {
        return this.allByFilter(idEvaluacionSeccion, idGrupoSeccion, idEvaluacionExpSup, EstadoEnum.ACT);
    }

    @Override
    public List<EvaluacionExpandida> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idEvaluacionExpSup, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(EvaluacionExpandida.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSeccion es", "es.grupoSeccion gs")
                .leftJoin("evaluacionSuperior esup")
                .orderBy("te.orden", "eva.numero");

        if (idEvaluacionExpSup == null) {
            sql.isNull("esup.id");
        }
        if (idEvaluacionSeccion != null) {
            sql.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sql.filter("gs.id", idGrupoSeccion);
        }
        if (idEvaluacionExpSup != null) {
            sql.filter("esup.id", idEvaluacionExpSup);
        }

        if (estadoEnum != null) {
            sql.filter("eva.estado", estadoEnum.name());
        }

        List<EvaluacionExpandida> evaluacionesExpandidas = all(sql);

        for (EvaluacionExpandida evaExpAbuelo : evaluacionesExpandidas) {
            evaExpAbuelo.setEvaluacionesExpandidas(allHijas(evaExpAbuelo));
            for (EvaluacionExpandida evaExpPadre : evaExpAbuelo.getEvaluacionesExpandidas()) {
                evaExpPadre.setEvaluacionesExpandidas(allHijas(evaExpPadre));
            }
        }

        return evaluacionesExpandidas;
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
        Octavia sql = Octavia.query()
                .from(EvaluacionExpandida.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSeccion es", "es.grupoSeccion gs")
                .leftJoin("evaluacionSuperior esup")
                .filter("es.id", evalSecc);

        return all(sql);
    }

    @Override
    public List<EvaluacionExpandida> allByGpoSeccionPlan(GrupoSeccion gpoSeccion, PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(EvaluacionExpandida.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSeccion es", "es.grupoSeccion gs", "es.planCalificacion p")
                .leftJoin("evaluacionSuperior esup")
                .filter("gs.id", gpoSeccion)
                .filter("p.id", plan);

        return all(sql);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(EvaluacionExpandida.class.getName()).append(" evx ")
                .append(" WHERE EXISTS ( ")
                .append("   SELECT 1 FROM ").append(EvaluacionSeccion.class.getName()).append(" evs ")
                .append("     JOIN evs.grupoSeccion gs ")
                .append("     JOIN gs.cicloAcademico ci ")
                .append("    WHERE ci.id = :CICLO ")
                .append("      AND evx.evaluacionSeccion.id = evs.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
