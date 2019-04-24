package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.LeccionReprogramada;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AsistenciaAcademicaService {

    List<Date> findStartEndDateReschedule(Seccion seccion, Docente docente, CicloAcademico cicloAcademico);

    TemaLeccion findTemaLeccionSeccionDocenteFecha(Seccion seccion, Docente docente, DateTime today);

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion, Docente docente, DateTime today);

    Seccion findSeccionDia(Seccion seccion, DateTime today);

    Seccion findSeccion(Long idSeccion);

    void saveInasistencia(TemaLeccion temaLeccion, Docente docente, CicloAcademico cicloAcademico, DataSessionPivot ds);

    void updateInasistencia(TemaLeccion temaLeccion, Docente docente, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<TemaLeccion> allTemaLeccionBySeccionDocenteDyna(Seccion seccion, Docente docente, DynatableFilter filter);

    TemaLeccion findTemaLeccion(Long idTemaLeccion);

    List<TemaLeccion> allTemaLeccionBySeccion(Seccion seccion);

    List<LeccionReprogramada> allLeccionReprogramadaBySeccion(Seccion seccion);

    void saveReprogramacion(LeccionReprogramada leccionReprogramada, Usuario usuario, Docente docente, CicloAcademico cicloAcademico);

    List<Aula> searchAulaByName(String nombre);

    List<GrupoSeccion> allGposSeccionesByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

}
