package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface SubvencionViajesService {

    List<DepartamentoAcademico> allDptosAcademicos(DataSessionPivot ds);

    List<ViajeCurso> allDynatbleByDocente(Docente docente, List<DepartamentoAcademico> dptos, CicloAcademico ciclo, DynatableFilter filter);

    List<Curso> allCursos(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    List<Seccion> allSecciones(Curso curso, Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    List<Alumno> allAlumnos(Seccion seccion, DataSessionPivot ds);

    void saveViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void updateViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void solicitarAprobarViaje(ViajeCurso viajeCurso, DataSessionPivot ds);

    void aprobarViaje(ViajeCurso viajeCurso, DataSessionPivot ds);

    void aprobarJustificacion(ViajeCurso viajeCurso, DataSessionPivot ds);

}
