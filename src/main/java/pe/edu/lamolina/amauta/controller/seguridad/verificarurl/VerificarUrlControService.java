package pe.edu.lamolina.amauta.controller.seguridad.verificarurl;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface VerificarUrlControService {

    Boolean accesoSessionUrl(DataSessionPivot ds, String rutaModulo);

}
