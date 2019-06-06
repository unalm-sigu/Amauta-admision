package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cancelarseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface CancelarSeccionService {

    List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion);

}
