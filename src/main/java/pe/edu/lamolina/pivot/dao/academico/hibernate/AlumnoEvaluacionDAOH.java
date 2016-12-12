package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
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

@Repository
public class AlumnoEvaluacionDAOH extends AbstractDAO<AlumnoEvaluacion> implements AlumnoEvaluacionDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AlumnoEvaluacionDAOH() {
        super();
        setClazz(AlumnoEvaluacion.class);
    }

    @Override
    public List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva");
        sqlUtil.parents("evaluacion eva");
        sqlUtil.parents("_eva.evaluacionSeccion es", "_eva.tipoEvaluacion te", "left _eva.seccionResponsable sr");
        sqlUtil.parents("_es.grupoSeccion gs");
        if (idEvaluacionSeccion != null) {
            sqlUtil.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sqlUtil.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sqlUtil.filter("sr.id", idGrupoSeccion);
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
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aeva")
                .parents("evaluacion eva", "_eva.tipoEvaluacion tEva", "alumno alu",
                        "_eva.seccionResponsable sec", "_sec.grupoSeccion gs", "_gs.curso cur", "_gs.cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("cur.id", curso)
                .filter("alu.id", alumno);

        return all(sqlUtil);
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
}
