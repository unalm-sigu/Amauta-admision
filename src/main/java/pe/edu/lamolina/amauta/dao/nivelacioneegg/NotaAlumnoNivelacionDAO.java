package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface NotaAlumnoNivelacionDAO extends EasyDAO<NotaAlumnoNivelacion> {

    List<NotaAlumnoNivelacion> allByCiclo(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allByAlumnoNivelacion(AlumnoNivelacion alumnoNiv);

    List<NotaAlumnoNivelacion> allByAlumnosNivelacion(List<AlumnoNivelacion> alumnosNiv);

    int saveList(List<NotaAlumnoNivelacion> notasAlumnos);

    int updateList(List<NotaAlumnoNivelacion> notasAlumnos, String... columnas);

}
