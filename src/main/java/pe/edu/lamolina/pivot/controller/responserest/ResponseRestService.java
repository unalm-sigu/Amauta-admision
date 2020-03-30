package pe.edu.lamolina.pivot.controller.responserest;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResponseRestService {

    Parametro findParametro(ParametrosSistemasEnum parametrosSistemasEnum);

    TokenIngresante createTokenForAlumno(Alumno alumno, DataSessionPivot ds);

    TokenIngresante createToken(Persona persona, DataSessionPivot ds);

    TokenIngresante createToken(DataSessionPivot ds);

    JsonResponse retirarMatriculaCurso(MatriculaCurso matriculaCurso, DataSessionPivot ds, EstadoMatriculaEnum estadoEnum, TokenIngresante token);

    JsonResponse matricularSeccion(Alumno alumno, Seccion seccion, DataSessionPivot ds, TokenIngresante token);

    JsonResponse matricularSeccionReservada(Alumno alumno, Seccion seccion, DataSessionPivot ds, TokenIngresante token);

    JsonResponse retirarMatriculaCiclo(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse generarAporte(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse generarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse generarAporteSegundaCarrera(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse eliminarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse ampliarVacante(Seccion seccion, Integer variacion, DataSessionPivot ds, TokenIngresante token);

    JsonResponse anularBoletas(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse downloadHistorial(Alumno alumno, CicloAcademico academico, Parametro paramRutaMatricula, DataSessionPivot ds, TokenIngresante token);

    JsonResponse eliminarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse generarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    JsonResponse eliminarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte, TokenIngresante token);

    JsonResponse modificarAporte(MatriculaResumen matriculaResumen, DataSessionPivot ds, Aporte aporte, TokenIngresante token);

    JsonResponse agregarAporte(Aporte aporte, MatriculaResumen matriculaResumen, DataSessionPivot ds, TokenIngresante token);

    public JsonResponse limpiarCache(DataSessionPivot ds, TokenIngresante token);
}
