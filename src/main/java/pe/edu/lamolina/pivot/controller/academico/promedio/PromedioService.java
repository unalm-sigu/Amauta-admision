package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PromedioService {

    void trasladarInformcionForHistorial(MatriculaResumen matriculaResumen, List<MatriculaCurso> matriculasCurso, List<MatriculaSeccion> matriculasSeccion, Usuario usuario);

    void promedio(MatriculaCurso matriculaCurso, DataSessionPivot ds, boolean calcularSituacionAcadFinal);

    void promediarAllCicloAsync(Alumno alumno, CicloAcademico cicloActivo, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds);

    void promediarAllCicloSync(Alumno alumno, CicloAcademico cicloActivo, List<AlumnoCicloCurso> allOperativesByModalidadEstudio, DataSessionPivot ds);

    void trasladoPromediosSource(MatriculaCurso matriculaCurso, DataSessionPivot ds);

    void generarHistorialNotas(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds);

    void calulcarSituacionAcademica(Alumno alumno, DataSessionPivot ds);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno);

    Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno);

}
