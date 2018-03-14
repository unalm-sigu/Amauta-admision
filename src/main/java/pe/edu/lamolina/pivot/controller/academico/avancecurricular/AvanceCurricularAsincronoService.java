package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularAsincronoService {

    void procesarAlumno(
            Alumno alumno,
            Map<Long, CursoCurricula> cursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            DataSessionPivot ds);

    void procesarAlumnoSincrono(
            Alumno alumno,
            Map<Long, CursoCurricula> cursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            DataSessionPivot ds);
    
    void deleteAllAlumnoCursoSimultaneoByAlumno(Alumno alumno);

    void deleteAllAlumnoCursoCurriculaByAlumno(Alumno alumno);
}
