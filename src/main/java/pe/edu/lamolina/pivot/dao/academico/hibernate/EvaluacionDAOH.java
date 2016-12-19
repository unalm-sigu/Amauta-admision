package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

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
        sqlUtil.parents("tipoEvaluacion te", "evaluacionSeccion es", "seccionResponsable sr", "left evaluacionSuperior esup");
        sqlUtil.parents("_es.planCalificacion pc", "_es.sistemaNotas sn");
        sqlUtil.parents("seccionResponsable sec", "_sec.grupoSeccion gs", "_gs.curso", "_gs.cicloAcademico", "left _esup.tipoEvaluacion tesupe");
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
                .parents("evaluacionSeccion", "tipoEvaluacion", "left evaluacionSuperior evaSup", "seccionResponsable sr")
                .filterIn("sr.id", secciones);
        sqlUtil.filterIsNull("evaSup");

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
    public List<Evaluacion> allByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("evaluacionSeccion es", "tipoEvaluacion", "left evaluacionSuperior evaSup", "seccionResponsable sr")
                .filter("es.id", evaluacionSeccion.getId());
        sqlUtil.filterIsNull("evaSup");

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

}
