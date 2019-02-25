package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioSeccionService {

    void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds);

    void asignarHorasAdicionales(Seccion seccion, DataSessionPivot ds);

}
