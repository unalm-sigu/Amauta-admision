package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class EvaluacionDAOH extends AbstractDAO<Evaluacion> implements EvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionDAOH() {
        super();
        setClazz(Evaluacion.class);
    }

    @Override
    public Evaluacion find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("tipoEvaluacion te", "evaluacionSeccion es", "seccionResponsable sr", "left evaluacionSuperior esup", "evaluacionExpandida eex", "left docenteEvaluador de");
        sqlUtil.parents("_es.planCalificacion pc", "_es.sistemaNotas sn");
        sqlUtil.parents("_sr.grupoSeccion gs", "_gs.curso", "_gs.cicloAcademico", "left _esup.tipoEvaluacion tesupe");
        sqlUtil.filter("eva.id", id);
        sqlUtil.filter("eex.estado", EstadoEnum.ACT.name());
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
    public List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacionExpandida) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("evaluacionSeccion es", "tipoEvaluacion te", "left seccionResponsable sr", "evaluacionExpandida exx", "left docenteEvaluador de");
        sqlUtil.parents("_es.grupoSeccion gs");
        sqlUtil.parents("left evaluacionSuperior esup");

        sqlUtil.filter("exx.estado", EstadoEnum.ACT.name());

        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sqlUtil.filter("sr.id", idSeccion);
        }
        if (idEvaluacionExpandida != null) {
            sqlUtil.filter("exx.id", idEvaluacionExpandida);
        }
        if (idEvaluacionExpandida == null) {
            sqlUtil.filterIsNull("esup.id");
        }
        //sqlUtil.orderBy("te.nombre", "eva.numero");
        List<Evaluacion> lstEvaluaciones = this.all(sqlUtil);
        if (lstEvaluaciones != null && !lstEvaluaciones.isEmpty()) {
            for (Evaluacion objEvaluacion : lstEvaluaciones) {
                for (Evaluacion eva : objEvaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                }
            }
        }
        return lstEvaluaciones;
    }

    @Override
    public List<Evaluacion> allBySecciones(List<Seccion> secciones) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionExpandida ee", "evaluacionSeccion", "tipoEvaluacion", "left evaluacionSuperior evaSup", "seccionResponsable sr")
                .filterIn("sr.id", secciones);
        sqlUtil.filterIsNull("evaSup");
        sqlUtil.filter("ee.estado", EstadoEnum.ACT.name());
        List<Evaluacion> evaluaciones = this.all(sqlUtil);
        for (Evaluacion evaluacion : evaluaciones) {
            if (evaluacion.getEvaluaciones() != null) {
                for (Evaluacion eva : evaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                    eva.getTipoEvaluacion().getNombre();
                }
            }
        }
        return evaluaciones;
    }

    @Override
    public List<Evaluacion> allBySeccion(Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "left evaluacionSuperior evaSup", "seccionResponsable sr")
                .filter("sr.id", seccion.getId())
                .orderBy("te.orden", "ev.numero");
        sqlUtil.filterIsNull("evaSup");
        sqlUtil.filter("ee.estado", EstadoEnum.ACT.name());
        List<Evaluacion> evaluaciones = this.all(sqlUtil);
        for (Evaluacion evaluacion : evaluaciones) {
            if (evaluacion.getEvaluaciones() != null) {
                for (Evaluacion eva : evaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                    eva.getTipoEvaluacion().getNombre();
                }
            }
        }
        return evaluaciones;
    }

    @Override
    public Long countEvaluacionesFaltantesByGrupo(Long idGrupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaCountSql("ev");
        sqlUtil.parents("seccionResponsable sr");
        sqlUtil.parents("_sr.grupoSeccion gs");
        sqlUtil.filter("gs.id", idGrupoSeccion);
        sqlUtil.filterIsNull("ev.fechaIngresoNota");
        return this.count(sqlUtil);
    }

    @Override
    public void deleteByEvaluacionExpandida(Long idEvaluacionExpandida) {
        String strQuery = "delete from Evaluacion eva where eva.evaluacionExpandida.id=:prm_evaluacion_exp";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("prm_evaluacion_exp", idEvaluacionExpandida);
        query.executeUpdate();
    }

    @Override
    public void updateDocenteEvaluador(Evaluacion evaluacion, Docente docente) {
        String strQuery = "update  Evaluacion eva set eva.docenteEvaluador.id=:prm_docente where eva.id=:prm_id";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setParameter("prm_docente", evaluacion.getDocenteEvaluador().getId());
        query.setParameter("prm_id", evaluacion.getId());
        query.executeUpdate();
    }

    @Override
    public Evaluacion findByEvalExpSeccion(Long evaluacionExpansion, Long seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("eva");
        sqlUtil.parents("tipoEvaluacion te", "evaluacionSeccion es", "seccionResponsable sr", "left evaluacionSuperior esup", "evaluacionExpandida eex");
        sqlUtil.filter("eex.id", evaluacionExpansion);
        sqlUtil.filter("sr.id", seccion);
        Evaluacion evaluacion = this.find(sqlUtil);
        if (evaluacion != null) {
            if (evaluacion.getEvaluaciones() != null) {
                for (Evaluacion eva : evaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                }
            }
        }
        return evaluacion;
    }

    @Override
    public List<Evaluacion> allByEvaluacionSeccion(EvaluacionSeccion evalSecc) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionSeccion es")
                .filter("es.id", evalSecc);

        return all(sqlUtil);
    }

    @Override
    public List<Evaluacion> allByEvaluacionesByExpandidas(List<EvaluacionExpandida> evaluacionesExp) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionSeccion es", "evaluacionExpandida ee", "seccionResponsable s", "left docenteEvaluador de")
                .filterIn("ee.id", evaluacionesExp);

        return all(sqlUtil);
    }

    @Override
    public List<Evaluacion> allByEvaluacionExpandidaSecciones(EvaluacionExpandida evaluacion, List<Seccion> secciones) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionSeccion es", "evaluacionExpandida ee", "left _ee.evaluacionSuperior ees", "seccionResponsable s", "left docenteEvaluador de")
                //  .filterIsNull("ees.id")
                .filter("ee.id", evaluacion)
                .filterIn("s.id", secciones);

        return all(sqlUtil);
    }

    @Override
    public void deleteEvaluacionesByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion) {
        StringBuilder strbDeleteAlumnoEvaluacion = new StringBuilder(" delete from aca_alumno_evaluacion where id_evaluacion in ( ");
        strbDeleteAlumnoEvaluacion.append(" select id from aca_evaluacion where id_evaluacion_expandida in ( ");
        strbDeleteAlumnoEvaluacion.append(" Select id from aca_evaluacion_expandida where id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteAlumnoEvaluacion.append("))");

        SQLQuery query = getCurrentSession().createSQLQuery(strbDeleteAlumnoEvaluacion.toString());
        query.setParameter("prm_evaluacion_seccion", evaluacionSeccion.getId());
        query.executeUpdate();

        StringBuilder strbDeleteReclamoNota = new StringBuilder(" delete from aca_reclamo_nota where id_evaluacion in ( ");
        strbDeleteReclamoNota.append(" Select id from aca_evaluacion where  id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteReclamoNota.append(");");

        query = getCurrentSession().createSQLQuery(strbDeleteReclamoNota.toString());
        query.setParameter("prm_evaluacion_seccion", evaluacionSeccion.getId());
        query.executeUpdate();

        List<Evaluacion> evaluaciones = allByFilter(evaluacionSeccion.getId(), null, null, null);

        List<Long> lstEvaNietasId = new ArrayList<>();
        List<Long> lstEvaHijasId = new ArrayList<>();
        List<Long> lstEvaPadresId = new ArrayList<>();

        for (Evaluacion evaluacione : evaluaciones) {
            lstEvaPadresId.add(evaluacione.getId());
            for (Evaluacion evaluacione1 : evaluacione.getEvaluaciones()) {
                lstEvaHijasId.add(evaluacione1.getId());
                for (Evaluacion evaluacione2 : evaluacione1.getEvaluaciones()) {
                    lstEvaNietasId.add(evaluacione2.getId());
                }
            }
        }
        StringBuilder strbDeleteEvaluacionesHijas = new StringBuilder(" delete from aca_evaluacion where id_evaluacion_expandida in ( ");
        strbDeleteEvaluacionesHijas.append(" :prm_evas ");
        strbDeleteEvaluacionesHijas.append(") ");

        if (!lstEvaNietasId.isEmpty()) {
            query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesHijas.toString());
            query.setParameterList("prm_evas", lstEvaNietasId);
            query.executeUpdate();
        }

        if (!lstEvaHijasId.isEmpty()) {
            query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesHijas.toString());
            query.setParameterList("prm_evas", lstEvaHijasId);
            query.executeUpdate();
        }

        if (!lstEvaPadresId.isEmpty()) {
            query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesHijas.toString());
            query.setParameterList("prm_evas", lstEvaPadresId);
            query.executeUpdate();
        }

        /*
        StringBuilder strbDeleteEvaluacionesHijas = new StringBuilder(" delete from aca_evaluacion where id_evaluacion_expandida in ( ");
        strbDeleteEvaluacionesHijas.append(" Select id from aca_evaluacion_expandida where id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteEvaluacionesHijas.append(") and id_evaluacion_superior is not null");

        query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesHijas.toString());
        query.setParameter("prm_evaluacion_seccion", evaluacionSeccion.getId());
        query.executeUpdate();

        StringBuilder strbDeleteEvaluacionesPadres = new StringBuilder(" delete from aca_evaluacion where id_evaluacion_expandida in ( ");
        strbDeleteEvaluacionesPadres.append(" Select id from aca_evaluacion_expandida where id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteEvaluacionesPadres.append(")");

        query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesPadres.toString());
        query.setParameter("prm_evaluacion_seccion", evaluacionSeccion.getId());
        query.executeUpdate();

        StringBuilder strbDeleteEvaluacionesExpadidas = new StringBuilder(" delete from aca_evaluacion where id_evaluacion_expandida in (");
        strbDeleteEvaluacionesExpadidas.append(" Select id from aca_evaluacion_expandida where id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteEvaluacionesExpadidas.append(" )");

        query = getCurrentSession().createSQLQuery(strbDeleteEvaluacionesExpadidas.toString());
        query.setParameter("prm_evaluacion_seccion", evaluacionSeccion.getId());
        query.executeUpdate();
         */
    }

}
