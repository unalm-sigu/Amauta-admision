package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularAsincronoService {

    void procesarAlumno(Alumno alumno, DataSessionPivot ds);

    void deleteAllAlumnoCursoSimultaneoByAlumno(Alumno alumno);
}
