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
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioService {

    void trasladarInformcionForHistorial(
            MatriculaResumen matriculaResumen,
            List<MatriculaCurso> matriculasCurso,
            List<MatriculaSeccion> matriculasSeccion,
            // List<AlumnoCicloCurso> allAlumnoCicloCurso,
            DataSessionPivot ds, boolean calcularSituacion);

    void promedio(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean calcularSituacionAcadFinal);

    void promediarAllCicloAsync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds);

    int promediarAllCicloSync(
            Alumno alumno,
            CicloAcademico cicloActivo,
            Egresado egresado,
            List<CicloAcademico> ciclos,
            List<AlumnoCiclo> alumnoCiclos,
            List<AlumnoCicloCurso> allOperativesByModalidadEstudio,
            List<AlumnoCicloCurso> allAlumnoCicloCurso,
            List<Reincorporacion> allReincorporacionesByAlumno,
            DataSessionPivot ds,boolean throwError);

    void trasladoPromediosSource(MatriculaCurso matriculaCurso, DataSessionPivot ds);

    void generarHistorialNotas(
            Alumno alumno,
            Egresado egresado,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds);

    void calcularSituacionAcademica(Alumno alumno, DataSessionPivot ds);

    void calulcarSituacionAcademicaNewSession(Alumno alumno, Egresado egresado, DataSessionPivot ds);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno);

    Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno);

    void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds);

}
