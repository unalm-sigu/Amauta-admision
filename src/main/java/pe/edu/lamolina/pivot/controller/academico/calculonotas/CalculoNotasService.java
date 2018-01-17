package pe.edu.lamolina.pivot.controller.academico.calculonotas;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CalculoNotasService {

    void calcularNotasLista(List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds);

    void recalcularAllResumenEvalAlumno(Alumno alumno, GrupoSeccion grupoSeccion, int envio, DataSessionPivot ds);

    void calcularNotasAlumno(Alumno alumno, GrupoSeccion grupoSeccion, Curso curso, CicloAcademico ciclo, DataSessionPivot ds);

}
