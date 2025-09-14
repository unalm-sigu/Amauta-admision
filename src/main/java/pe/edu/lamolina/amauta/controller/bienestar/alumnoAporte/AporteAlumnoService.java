package pe.edu.lamolina.amauta.controller.bienestar.alumnoAporte;

import java.util.List;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;

public interface AporteAlumnoService {

    void generarAportes(Alumno alumno, CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteCarnet(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteSegundaCarrera(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void quitarAporteCarnet(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void quitarAporteDuplicadoCarnet(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void generarAporteDuplicadoCarnet(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    JsonResponse getModificarAporte(CicloAcademico ciclo, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    JsonResponse getEliminarAporte(CicloAcademico ciclo, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    JsonResponse getAnularOmisoVotar(List<AporteAlumnoCiclo> aportesAlumno, MatriculaResumen matricula, CicloAcademico ciclo, DataSessionPivot ds);

    JsonResponse getRecrearDeudas(CicloAcademico ciclo, Alumno alumno, DataSessionPivot ds);

    void modificarAporte(CicloAcademico ciclo, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    void eliminarAporte(CicloAcademico ciclo, MatriculaResumen matriculaResumen, Aporte aporte, DataSessionPivot ds);

    void generarAporteSegundaCarreraDeuda(CicloAcademico ciclo, MatriculaResumen matriculaResumen, DataSessionPivot ds);
}
