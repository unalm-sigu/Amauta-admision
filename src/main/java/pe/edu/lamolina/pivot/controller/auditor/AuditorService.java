package pe.edu.lamolina.pivot.controller.auditor;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SistemaNotas;
import pe.edu.lamolina.model.enums.LoggerAccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AuditorService {

    void auditTrasladoNotasToHistorial(Alumno alumno, Curso curso, CicloAcademico ciclo, MatriculaCurso matriculaCurso, DataSessionPivot ds, Exception exc);

    void auditSaveNotas(LoggerAccionEnum loggerAccionEnum, Evaluacion evaluacion, PlanCalificacion planCalificacion, SistemaNotas sistemaNotas, Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds);

    void auditSaveNotas(Evaluacion evaluacion, PlanCalificacion planCalificacion, SistemaNotas sistemaNotas, Seccion seccion, Curso curso,
            CicloAcademico cicloAcademico,
            List<Evaluacion> evaluacionesBySeccionFinal,
            List<MatriculaSeccion> matriculasSeccionByFilter,
            Map<String, AlumnoEvaluacion> notas,
            Map matriculaCursoMap,
            DataSessionPivot ds);

    void auditPromediarAlumno(Alumno alumno, CicloAcademico cicloAcademico, DataSessionPivot ds, Exception exc);

    void saveGrupoSeccion(GrupoSeccion gsec, DataSessionPivot ds);
}
