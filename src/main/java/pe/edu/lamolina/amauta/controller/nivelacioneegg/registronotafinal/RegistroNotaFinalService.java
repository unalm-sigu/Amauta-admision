package pe.edu.lamolina.amauta.controller.nivelacioneegg.registronotafinal;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface RegistroNotaFinalService {

    CursoNivelacion findSeccion(CursoNivelacion cursoNivelacion, Docente docente, CicloAcademico ciclo);

    List<ExamenCursoNivelacion> allExamenes(CursoNivelacion seccion);

    List<NotaAlumnoNivelacion> allAlumnos(DynatableFilter filter, CursoNivelacion cursoNivelacion);

    void abrirActa(ExamenCursoNivelacion examenCurso, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    void cerrarActa(ExamenCursoNivelacion examenCurso, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    void registrarNota(ExamenAlumnoNivelacion notaAlumno, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    void cerrarNotas(CursoNivelacion cursoNivelacion, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

}
