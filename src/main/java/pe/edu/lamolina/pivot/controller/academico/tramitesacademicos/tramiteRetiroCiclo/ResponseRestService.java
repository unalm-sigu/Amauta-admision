package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResponseRestService {

    JsonResponse updateRest(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporte(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void createToken(DataSessionPivot ds);

    JsonResponse generarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse eliminarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse ampliarVacante(Seccion seccion, Integer variacion);

    JsonResponse anularBoletas(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public JsonResponse downloadHistorial(Alumno alumno, Usuario usuario, CicloAcademico academico, Parametro paramRutaMatricula);

    JsonResponse eliminarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);
}
