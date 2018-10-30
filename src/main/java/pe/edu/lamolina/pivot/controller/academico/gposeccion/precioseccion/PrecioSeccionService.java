package pe.edu.lamolina.pivot.controller.academico.gposeccion.precioseccion;

import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioSeccionService {

    void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds);

}
