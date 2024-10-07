package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

public interface AlumnosNivelacionService {

    List<AlumnoNivelacion> allAlumnosByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    void createAlumnos(CicloAcademico ciclo, DataSessionPivot ds);

    void revisarTodosAlumnos(CicloAcademico ciclo, DataSessionPivot ds);

    void revisarAlumno(AlumnoNivelacion alumnoNiv, DataSessionPivot ds);

    void addAlumno(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

}
