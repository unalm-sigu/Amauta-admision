package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AsistenciaAcademicaService {

    TemaLeccion findTemaLeccionSeccionDocenteFecha(Seccion seccion, Docente docente, DateTime today);

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion, Docente docente, DateTime today);

    Seccion findSeccionDia(Seccion seccion, DateTime today);

    Seccion findSeccion(Long idSeccion);

    void saveInasistencia(TemaLeccion temaLeccion, Docente docente, Usuario usuario, CicloAcademico cicloAcademico);

    void updateInasistencia(TemaLeccion temaLeccion, Docente docente, Usuario usuario, CicloAcademico cicloAcademico);

    List<TemaLeccion> allTemaLeccionBySeccionDocenteDyna(Seccion seccion, Docente docente, DynatableFilter filter);

}
