package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.util.List;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface AsistenciaAcademicaService {

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion);

}
