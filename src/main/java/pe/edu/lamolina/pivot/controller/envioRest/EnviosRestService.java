package pe.edu.lamolina.pivot.controller.envioRest;

import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EnviosRestService {

   void modificarDescuento(Seccion seccion, DataSessionPivot ds);
}
