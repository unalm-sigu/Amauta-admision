package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import static pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum.MAT;

@Repository
public class AlumnoEvaluacionDAOH extends AbstractDAO<AlumnoEvaluacion> implements AlumnoEvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AlumnoEvaluacionDAOH() {
        super();
        setClazz(AlumnoEvaluacion.class);
    }

    @Override
    public List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion) {
        return this.allByFilter(idEvaluacionSeccion, idGrupoSeccion, idSeccion, idEvaluacion, null);
    }

    @Override
    public List<AlumnoEvaluacion> allByEvaluacionExp(Long idEvaluacionExpandida) {
        return this.allByFilter(null, null, null, null, idEvaluacionExpandida);
    }

    @Override
    public List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion, Long idEvaluacionExpandida) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva");
        sqlUtil.parents("evaluacion eva");
        sqlUtil.parents("_eva.evaluacionSeccion es", "_eva.tipoEvaluacion te", "left _eva.seccionResponsable sr", "left _eva.evaluacionSuperior evaSup");
        sqlUtil.parents("_es.grupoSeccion gs", "left _evaSup.tipoEvaluacion te2", "_eva.evaluacionExpandida evaex");
        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sqlUtil.filter("sr.id", idSeccion);
        }
        if (idEvaluacion != null) {
            sqlUtil.filter("eva.id", idEvaluacion);
        }
        if (idEvaluacionExpandida != null) {
            sqlUtil.filter("evaex.id", idEvaluacionExpandida);
        }
        List<AlumnoEvaluacion> lstEvaluaciones = this.all(sqlUtil);
        return lstEvaluaciones;
    }

    @Override
    public List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idALumno, Long idCurso, Long idCicloAcademico, String orderBy) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva");
        sqlUtil.parents("evaluacion eva", "alumno alu", "usuarioIngresoNota ureg");
        sqlUtil.parents("_eva.evaluacionSeccion es", "_eva.tipoEvaluacion te", "left _eva.seccionResponsable sr");
        sqlUtil.parents("_es.grupoSeccion gs", "_ureg.persona per", "_gs.curso cur", "_gs.cicloAcademico cic");
        if (orderBy != null) {
            sqlUtil.orderBy(orderBy);
        }
        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sqlUtil.filter("sr.id", idSeccion);
        }
        if (idALumno != null) {
            sqlUtil.filter("alu.id", idALumno);
        }
        if (idCurso != null) {
            sqlUtil.filter("cur.id", idCurso);
        }
        if (idCicloAcademico != null) {
            sqlUtil.filter("cic.id", idCicloAcademico);
        }
        List<AlumnoEvaluacion> lstEvaluaciones = this.all(sqlUtil);
        return lstEvaluaciones;
    }

    @Override
    public List<AlumnoEvaluacion> allBySeccion(Long idSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva")
                .parents("evaluacion eva", "alumno alu")
                .parents("_eva.evaluacionSeccion es", "_eva.tipoEvaluacion te", "left _eva.seccionResponsable sr")
                .parents("_es.grupoSeccion gs")
                .filter("sr.id", idSeccion);

        return all(sqlUtil);
    }

    @Override
    public List<AlumnoEvaluacion> allByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(AlumnoEvaluacion.class.getName()).append(" as ae ");
        sql.append("  join fetch ae.evaluacion eva ");
        sql.append("  join fetch eva.evaluacionExpandida evae");
        sql.append("  join fetch eva.tipoEvaluacion tEva ");
        sql.append("  join fetch ae.alumno alu ");
        sql.append("  join fetch eva.seccionResponsable sec ");
        sql.append("  join fetch sec.grupoSeccion gs ");
        sql.append("  join fetch gs.curso cur ");
        sql.append("  join fetch gs.cicloAcademico ca ");
        sql.append("  left join fetch eva.evaluacionSuperior evaSup ");
        sql.append("  left join fetch evaSup.tipoEvaluacion ");
        sql.append(" where ca.id = :CICLO ");
        sql.append("   and exists ( ");
        sql.append("       select ms.id ");
        sql.append("         from ").append(MatriculaSeccion.class.getName()).append(" ms ");
        sql.append("         join ms.matriculaResumen mr ");
        sql.append("        where mr.alumno.id = alu.id ");
        sql.append("          and mr.cicloAcademico.id = ca.id ");
        sql.append("          and ms.seccion.id = sec.id ");
        sql.append("          and ms.estado = :ESTADO ");
        sql.append("   ) ");
        sql.append("   and exists ( ");
        sql.append("       select mc.id ");
        sql.append("         from ").append(MatriculaCurso.class.getName()).append(" mc ");
        sql.append("         join mc.matriculaResumen mr ");
        sql.append("        where mr.alumno.id = alu.id ");
        sql.append("          and mr.cicloAcademico.id = ca.id ");
        sql.append("          and mc.curso.id = cur.id ");
        sql.append("          and mc.estado = :ESTADO ");
        sql.append("   ) ");
        sql.append(" and evae.estado = 'ACT' ");
        //sql.append(" and ae.estado = 'ACT' ");

        if (alumno != null) {
            sql.append("   and alu.id = :ALUMNO ");
        }
        if (curso != null) {
            sql.append("   and cur.id = :CURSO ");
        }

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.setString("ESTADO", MAT.name());

        if (alumno != null) {
            query.setLong("ALUMNO", alumno.getId());
        }
        if (curso != null) {
            query.setLong("CURSO", curso.getId());
        }

        return query.list();

    }

    @Override
    public AlumnoEvaluacion findByFilter(Long id, Long idEvaluacion, Long idAlumno) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva")
                .parents("evaluacion eva", "alumno alu")
                .parents("_eva.evaluacionSeccion es", "_eva.tipoEvaluacion te", "left _eva.seccionResponsable sr")
                .parents("_es.grupoSeccion gs");
        if (id != null) {
            sqlUtil.filter("aeva.id", id);
        }
        if (idEvaluacion != null) {
            sqlUtil.filter("eva.id", idEvaluacion);
        }
        if (idAlumno != null) {
            sqlUtil.filter("alu.id", idAlumno);
        }
        return find(sqlUtil);
    }

    @Override
    public void deleteByEvaluacion(Evaluacion evaluacion) {
        String strQuery = "delete from AlumnoEvaluacion eva where eva.evaluacion.id=:prm_evaluacion";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("prm_evaluacion", evaluacion.getId());
        query.executeUpdate();
    }

}
