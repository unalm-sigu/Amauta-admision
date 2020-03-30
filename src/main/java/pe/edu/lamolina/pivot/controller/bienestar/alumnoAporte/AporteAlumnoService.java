package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AporteAlumnoService {

    void generarAportes(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteSegundaCarrera(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void quitarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void quitarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse getModificarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    JsonResponse getEliminarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    void modificarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    void eliminarAporte(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);
}
