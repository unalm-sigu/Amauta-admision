package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.util.List;
import org.joda.time.DateTime;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface PromedioService {

    void procesarMatriculaResumen(MatriculaResumen matriculaResumen, List<MatriculaCurso> matriculasCurso, Usuario usuario);

    void promedio(MatriculaCurso matriculaCurso, Usuario usuario, boolean calcularSituacionAcadFinal);

    void promediarAllCicloAsync(Alumno alumno, Usuario usuario);

    void promediarAllCicloSync(Alumno alumno, Usuario usuario);

    void trasladoPromediosSource(MatriculaCurso matriculaCurso, Usuario usuario);

    void generarHistorialNotas(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            Usuario usuario,
            DateTime today);

    void calulcarSituacionAcademica(Alumno alumno, Usuario usuario);

    Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno);

    Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno);

}
