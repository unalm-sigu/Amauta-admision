package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AmpliacionVacanteRestService {

    JsonResponse validarAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

    JsonResponse matricularAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

    JsonResponse confirmarAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

    JsonResponse solicitarAmpliacionVacante(MatriculaSeccion matriculaSeccion, DataSessionPivot ds);

}
