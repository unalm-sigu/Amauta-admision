package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Repository
public class EvaluacionDAOH extends AbstractEasyDAO<Evaluacion> implements EvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EvaluacionDAOH() {
        super();
        setClazz(Evaluacion.class);
    }

    @Override
    public Evaluacion find(long id) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("tipoEvaluacion te", "evaluacionSeccion es", "seccionResponsable sr", "evaluacionExpandida eex")
                .join("es.planCalificacion pc", "es.sistemaNotas sn")
                .join("sr.grupoSeccion gs", "gs.curso", "gs.cicloAcademico")
                .leftJoin("evaluacionSuperior esup", "docenteEvaluador de", "esup.tipoEvaluacion tesupe")
                .filter("eva.id", id)
                .filter("eex.estado", EstadoEnum.ACT);

        Evaluacion evaluacion = find(sql);
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
    public List<Evaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacionExpandida) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionSeccion es", "tipoEvaluacion te", "evaluacionExpandida exx", "es.grupoSeccion gs")
                .leftJoin("evaluacionSuperior esup", "esup.tipoEvaluacion tesupe")
                .leftJoin("docenteEvaluador de", "seccionResponsable sr")
                .filter("eex.estado", EstadoEnum.ACT);

        if (idEvaluacionSeccion != null) {
            sql.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sql.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sql.filter("sr.id", idSeccion);
        }
        if (idEvaluacionExpandida != null) {
            sql.filter("exx.id", idEvaluacionExpandida);
        }
        if (idEvaluacionExpandida == null) {
            sql.isNull("esup.id");
        }

        List<Evaluacion> evaluaciones = all(sql);
        if (evaluaciones != null && !evaluaciones.isEmpty()) {
            for (Evaluacion objEvaluacion : evaluaciones) {
                for (Evaluacion eva : objEvaluacion.getEvaluaciones()) {
                    eva.getId();
                    eva.getTipoEvaluacion().getId();
                }
            }
        }
        return evaluaciones;
    }

    @Override
    public List<Evaluacion> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion", "tipoEvaluacion", "seccionResponsable sr")
                .leftJoin("evaluacionSuperior evaSup")
                .in("sr.id", secciones)
                .isNull("evaSup.id")
                .filter("ee.estado", EstadoEnum.ACT);

        List<Evaluacion> evaluaciones = this.all(sql);
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
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .leftJoin("evaluacionSuperior evaSup")
                .filter("sr.id", seccion)
                .isNull("evaSup.id")
                .filter("ee.estado", EstadoEnum.ACT);

        List<Evaluacion> evaluaciones = this.all(sql);

        for (Evaluacion evaluacionAbuelo : evaluaciones) {
            for (int j = evaluacionAbuelo.getEvaluaciones().size() - 1; j >= 0; j--) {
                Evaluacion evaluacionPadre = evaluacionAbuelo.getEvaluaciones().get(j);
                if (evaluacionPadre.getEvaluacionExpandida().isEstadoAnulado()) {
                    evaluacionAbuelo.getEvaluaciones().remove(j);
                } else {
                    for (int k = evaluacionPadre.getEvaluaciones().size() - 1; k >= 0; k--) {
                        Evaluacion evaluacionHija = evaluacionPadre.getEvaluaciones().get(k);
                        if (evaluacionHija.getEvaluacionExpandida().isEstadoAnulado()) {
                            evaluacionPadre.getEvaluaciones().remove(k);
                        }
                    }
                }
            }
        }

        return evaluaciones;
    }

    @Override
    public Long countEvaluacionesFaltantesByGrupo(Long idGrupoSeccion) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(Evaluacion.class, "eva")
                .join("seccionResponsable sr", "sr.grupoSeccion gs")
                .filter("gs.id", idGrupoSeccion)
                .isNull("eva.fechaIngresoNota");

        return (Long) sql.find(getCurrentSession());
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
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .leftJoin("evaluacionSuperior evaSup")
                .filter("ee.id", evaluacionExpansion)
                .filter("sr.id", seccion);

        Evaluacion evaluacion = find(sql);
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
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionSeccion es")
                .filter("es.id", evalSecc);

        return all(sql);
    }

    @Override
    public List<Evaluacion> allByEvaluacionesByExpandidas(List<EvaluacionExpandida> evaluacionesExp) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .leftJoin("docenteEvaluador de")
                .in("ee.id", evaluacionesExp);

        return all(sql);
    }

    @Override
    public List<Evaluacion> allByEvaluacionExpandidaSecciones(EvaluacionExpandida evaluacion, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .leftJoin("ee.evaluacionSuperior ees", "docenteEvaluador de")
                .filter("ee.id", evaluacion)
                .in("sr.id", secciones);

        return all(sql);
    }

    @Override
    public void deleteEvaluacionesByEvaluacionSeccion(EvaluacionSeccion evaluacionSeccion) {

        StringBuilder strbDeleteReclamoNota = new StringBuilder(" delete from aca_reclamo_nota where id_evaluacion in ( ");
        strbDeleteReclamoNota.append(" Select id from aca_evaluacion where  id_evaluacion_seccion=:prm_evaluacion_seccion ");
        strbDeleteReclamoNota.append(");");

        SQLQuery query = getCurrentSession().createSQLQuery(strbDeleteReclamoNota.toString());
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
        StringBuilder strbDeleteEvaluacionesHijas = new StringBuilder(" delete from aca_evaluacion where id in ( ");
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
    }

    @Override
    public List<Evaluacion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .join("ee.tipoEvaluacion", "es.grupoSeccion", "es.planCalificacion", "sr.grupoSeccion gs", "gs.cicloAcademico ci")
                .leftJoin("ee.evaluacionSuperior ees", "evaluacionSuperior", "docenteEvaluador de")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public List<Evaluacion> allByGrupoSeccionAlumno(GrupoSeccion grupoSeccion, Alumno alumno) {
        Octavia subquery = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("ms.seccion ss", "matriculaResumen mr", "mr.alumno alu")
                .filter("alu.id", alumno);

        Octavia sql = Octavia.query()
                .from(Evaluacion.class, "eva")
                .join("evaluacionExpandida ee", "evaluacionSeccion es", "tipoEvaluacion te", "seccionResponsable sr")
                .join("ee.tipoEvaluacion", "es.grupoSeccion gs", "es.planCalificacion")
                .leftJoin("ee.evaluacionSuperior", "evaluacionSuperior")
                .filter("gs.id", grupoSeccion)
                .filter("ee.estado", EstadoEnum.ACT)
                .exists(subquery)
                .linkedBy("sr.id", "ss.id");

        return all(sql);
    }

}
