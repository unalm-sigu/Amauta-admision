package pe.edu.lamolina.pivot.controller.academico.alumnosdocente;

import java.util.List;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface AlumnosDocenteService {

    Seccion findSeccion(Long idSeccion);

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion);

}
