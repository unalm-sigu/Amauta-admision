package pe.edu.lamolina.amauta.controller.academico.calculonotas;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface CalculoNotasService {

    void calcularNotasLista(List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds);

    void recalcularAllResumenEvalAlumno(Alumno alumno, GrupoSeccion grupoSeccion, int envio, DataSessionPivot ds);

    void calcularNotasAlumno(Alumno alumno, GrupoSeccion grupoSeccion, Usuario usuario); // Curso curso , CicloAcademico ciclo

    void calcularNotas(EvaluacionExpandida evaluacionExpandida, CicloAcademico cicloAcademico, Usuario usuario);

}
