package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AmpliacionVacanteService {

    List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico, Seccion seccion);

    void matricular(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds);

    void solicitarAmpliacion(Seccion seccion, AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<MatriculaSeccion> allSolicitudesBySeccion(Seccion seccion, DataSessionPivot ds);

    void aceptarSolicitudMatricula(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

    void rechazarSolicitudMatricula(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

}
