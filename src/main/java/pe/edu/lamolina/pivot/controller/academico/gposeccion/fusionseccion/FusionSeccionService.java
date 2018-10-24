package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Seccion;

public interface FusionSeccionService {

    List<Alumno> allAlumnoBySeccion(Seccion seccion);

    void trasladar(Fusion trasladoForm);

}
