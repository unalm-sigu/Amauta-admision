package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.nivelacioneegg.ExamenAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface ExamenAlumnoNivelacionDAO extends EasyDAO<ExamenAlumnoNivelacion> {

    List<ExamenAlumnoNivelacion> allByNotaAlumno(NotaAlumnoNivelacion nota);

    List<ExamenAlumnoNivelacion> allByNotasAlumnos(List<NotaAlumnoNivelacion> notasAlumnos);

    List<ExamenAlumnoNivelacion> allByExamen(ExamenCursoNivelacion examen);

}
