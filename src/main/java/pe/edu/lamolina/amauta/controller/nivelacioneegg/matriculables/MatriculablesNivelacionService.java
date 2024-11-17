package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;

public interface MatriculablesNivelacionService {

    List<AlumnoNivelacion> allAlumnosByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    void createAlumnos(CicloAcademico ciclo, DataSessionPivot ds);

    int revisarTodosAlumnos(CicloAcademico ciclo, DataSessionPivot ds);

    int revisarAlumno(AlumnoNivelacion alumnoNiv, DataSessionPivot ds);

    List<Alumno> searchAlumno(String nombre, DataSessionPivot ds);

    void addAlumno(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void deshabilitarAlumno(AlumnoNivelacion alumnoNiv, DataSessionPivot ds);

    void habilitarAlumno(AlumnoNivelacion alumnoNiv, DataSessionPivot ds);

}
