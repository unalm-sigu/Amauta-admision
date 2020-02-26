package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioService {

    void actasNotasHaciaHistorial(
            MatriculaResumen matriculaResumen,
            List<MatriculaCurso> matriculasCurso,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            DataSessionPivot ds, String token);

    void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds,
            String token,
            boolean throwError, boolean showError);

    int promediarAllCicloSync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds, boolean throwError, boolean showError);

    void calcularSituacionAcademica(Alumno alumno, DataSessionPivot ds);

    void calulcarSituacionAcademicaNewSession(Alumno alumno, Egresado egresado, DataSessionPivot ds);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno, Curso curso);

    Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno);

    Integer evaluateEstaAprobado(AlumnoCicloCurso alumnoCicloCurso, Alumno alumno);

    void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds);

    void verificarAlumnosNmat(CicloAcademico ciclo);

}
