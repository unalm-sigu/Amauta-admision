package pe.edu.lamolina.pivot.controller.auditor;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AuditorService {

    void auditSaveNotas(Evaluacion evaluacion, PlanCalificacion planCalificacion, SistemaNotas sistemaNotas, Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds);
}
