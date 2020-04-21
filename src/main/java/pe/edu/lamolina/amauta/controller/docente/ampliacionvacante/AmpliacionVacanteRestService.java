package pe.edu.lamolina.amauta.controller.docente.ampliacionvacante;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AmpliacionVacanteRestService {

    JsonResponse validarAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

    JsonResponse matricularAmpliacionVacante(Seccion seccion, Alumno alumno, DataSessionPivot ds);

    JsonResponse confirmarAmpliacionVacante(MatriculaSeccion matriculaSeccion, boolean esDocenteTCUR, DataSessionPivot ds);

    JsonResponse solicitarAmpliacionVacante(Seccion seccion, Alumno alumno, boolean esDocenteTCUR, DataSessionPivot ds);

    JsonResponse rechazarAmpliacionVacante(MatriculaSeccion matriculaSeccion, boolean esDocenteTCUR, DataSessionPivot ds);

}
