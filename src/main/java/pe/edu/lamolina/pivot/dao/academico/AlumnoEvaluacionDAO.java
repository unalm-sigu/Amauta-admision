package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Evaluacion;

public interface AlumnoEvaluacionDAO extends EasyDAO<AlumnoEvaluacion> {

    List<AlumnoEvaluacion> allByEvaluacionExp(Long idEvaluacionExpandida);

    List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion);

    List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idEvaluacion, Long idEvaluacionExpandida);

    List<AlumnoEvaluacion> allByFilter(Long idEvaluacionSeccion, Long idGrupoSeccion, Long idSeccion, Long idALumno, Long idCurso, Long idCicloAcademico, String orderBy);

    List<AlumnoEvaluacion> allBySeccion(Long idSeccion);

    List<AlumnoEvaluacion> allByAlumnoCursoCiclo(Alumno alumno, Curso curso, CicloAcademico ciclo, CicloAcademico cicloMod);

    AlumnoEvaluacion findByFilter(Long id, Long idEvaluacion, Long idAlumno);

    void deleteByEvaluacion(Evaluacion evaluacion);

}
