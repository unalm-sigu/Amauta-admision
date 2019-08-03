package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;

@Repository
public class AlumnoEvaluacionDAOH extends AbstractEasyDAO<AlumnoEvaluacion> implements AlumnoEvaluacionDAO {
    
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
        
        Octavia sql = Octavia.query()
                .from(AlumnoEvaluacion.class, "aeva")
                .join("alumno al", "al.modalidadEstudio")
                .join("evaluacion eva", "eva.evaluacionSeccion es", "eva.tipoEvaluacion te")
                .join("es.grupoSeccion gs", "eva.evaluacionExpandida evaex")
                .left("eva.seccionResponsable sr", "eva.evaluacionSuperior evaSup", "evaSup.tipoEvaluacion te2");
        
        if (idEvaluacionSeccion != null) {
            sql.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sql.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sql.filter("sr.id", idSeccion);
        }
        if (idEvaluacion != null) {
            sql.filter("eva.id", idEvaluacion);
        }
        if (idEvaluacionExpandida != null) {
            sql.filter("evaex.id", idEvaluacionExpandida);
        }
        
        return all(sql);
    }
    
    @Override
    public List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idALumno, Long idCurso, Long idCicloAcademico, String orderBy) {
        Octavia sql = Octavia.query()
                .from(AlumnoEvaluacion.class, "aeva")
                .join("evaluacion eva", "alumno alu", "usuarioIngresoNota ureg", "ureg.persona per")
                .join("eva.evaluacionSeccion es", "eva.tipoEvaluacion te", "es.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico cic")
                .left("eva.seccionResponsable sr", "eva.evaluacionSuperior evaSup", "evaSup.tipoEvaluacion te2");
        
        if (orderBy != null) {
            sql.orderBy(orderBy);
        }
        if (idEvaluacionSeccion != null) {
            sql.filter("es.id", idEvaluacionSeccion);
        }
        if (idGrupoSeccion != null) {
            sql.filter("gs.id", idGrupoSeccion);
        }
        if (idSeccion != null) {
            sql.filter("sr.id", idSeccion);
        }
        if (idALumno != null) {
            sql.filter("alu.id", idALumno);
        }
        if (idCurso != null) {
            sql.filter("cur.id", idCurso);
        }
        if (idCicloAcademico != null) {
            sql.filter("cic.id", idCicloAcademico);
        }
        
        return all(sql);
    }
    
    @Override
    public List<AlumnoEvaluacion> allBySeccion(Long idSeccion) {
        Octavia sql = Octavia.query()
                .from(AlumnoEvaluacion.class, "aeva")
                .join("evaluacion eva", "alumno alu")
                .join("eva.evaluacionSeccion es", "eva.tipoEvaluacion te", "es.grupoSeccion gs")
                .left("eva.seccionResponsable sr")
                .filter("sr.id", idSeccion);
        
        return all(sql);
    }
    
    @Override
    public List<AlumnoEvaluacion> allByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo, CicloAcademico cicloMod) {
        
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
        sql.append("          and mr.cicloAcademico.id = :CICLOMOD ");
        sql.append("          and ms.seccion.id = sec.id ");
        sql.append("          and ms.estado = :ESTADO ");
        sql.append("   ) ");
        sql.append("   and exists ( ");
        sql.append("       select mc.id ");
        sql.append("         from ").append(MatriculaCurso.class.getName()).append(" mc ");
        sql.append("         join mc.matriculaResumen mr ");
        sql.append("        where mr.alumno.id = alu.id ");
        sql.append("          and mr.cicloAcademico.id = :CICLOMOD ");
        sql.append("          and mc.curso.id = cur.id ");
        sql.append("          and mc.estado = :ESTADO ");
        sql.append("   ) ");
        sql.append(" and evae.estado = 'ACT' ");
        
        if (alumno != null) {
            sql.append("   and alu.id = :ALUMNO ");
        }
        if (curso != null) {
            sql.append("   and cur.id = :CURSO ");
        }
        
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.setLong("CICLOMOD", cicloMod.getId());
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
        Octavia sql = Octavia.query()
                .from(AlumnoEvaluacion.class, "aeva")
                .join("evaluacion eva", "alumno alu")
                .join("eva.evaluacionSeccion es", "eva.tipoEvaluacion te", "es.grupoSeccion g")
                .leftJoin("eva.seccionResponsable srs");
        
        if (id != null) {
            sql.filter("aeva.id", id);
        }
        if (idEvaluacion != null) {
            sql.filter("eva.id", idEvaluacion);
        }
        if (idAlumno != null) {
            sql.filter("alu.id", idAlumno);
        }
        return find(sql);
    }
    
    @Override
    public void deleteByEvaluacion(Evaluacion evaluacion) {
        String strQuery = "delete from AlumnoEvaluacion eva where eva.evaluacion.id=:prm_evaluacion";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("prm_evaluacion", evaluacion.getId());
        query.executeUpdate();
    }
    
}
