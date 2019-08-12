package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResponseRestService {

    public JsonResponse updateRest(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporte(Alumno alumno, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void createToken(DataSessionPivot ds);

    public JsonResponse generarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public JsonResponse eliminarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);
}
