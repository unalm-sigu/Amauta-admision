package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.math.BigDecimal;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResponseRestService {

    JsonResponse retirarMatriculaCurso(MatriculaCurso matriculaCurso, DataSessionPivot ds, EstadoMatriculaEnum estadoEnum);

    JsonResponse matricularSeccion(Alumno alumno, Seccion seccion, DataSessionPivot ds);

    JsonResponse retirarMatriculaCiclo(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporte(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void createToken(DataSessionPivot ds);

    JsonResponse generarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporteSegundaCarrera(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse eliminarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse ampliarVacante(Seccion seccion, Integer variacion, DataSessionPivot ds);

    JsonResponse anularBoletas(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse downloadHistorial(Alumno alumno, Usuario usuario, CicloAcademico academico, Parametro paramRutaMatricula);

    JsonResponse eliminarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse generarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse eliminarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte);
    
    JsonResponse modificarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte, BigDecimal monto);

    public JsonResponse agregarAporte(Aporte aporte, MatriculaResumen matriculaResumen, DataSessionPivot ds);
}
