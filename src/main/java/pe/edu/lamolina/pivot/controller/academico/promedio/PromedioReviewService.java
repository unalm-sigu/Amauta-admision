package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioReviewService {

    void trasladarInformcionForHistorial(MatriculaResumen matriculaResumen, List<MatriculaCurso> matriculasCurso, List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds, Map<Long, RetiroCiclo> mapRetiro,
            Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso,
            Map<Long, AlumnoCiclo> mapalumnoCiclo,
            SituacionAcademica situacionAcademicaComodin, boolean calcularSituacion);

    void promedio(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean calcularSituacionAcadFinal);

    void promediarAllCicloAsync(Alumno alumno, CicloAcademico cicloActivo, List<CicloAcademico> ciclos, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds);

    void promediarAllCicloSync(Alumno alumno, CicloAcademico cicloActivo, List<CicloAcademico> ciclos, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds);

    void trasladoPromediosSource(MatriculaCurso matriculaCurso, DataSessionPivot ds);

    void generarHistorialNotas(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            Map<Long, RetiroCiclo> mapRetiro,
            DataSessionPivot ds);

    void calulcarSituacionAcademica(Alumno alumno, DataSessionPivot ds);

    void calulcarSituacionAcademicaNewSession(Alumno alumno, DataSessionPivot ds);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno);

    Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno);

    void saveCerrarActaAsync(List<Alumno> alumnos, DataSessionPivot ds);

}
