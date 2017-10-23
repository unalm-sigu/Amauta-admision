package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import javax.servlet.http.HttpServletRequest;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface VerificadorService {

    void revisarPermiso(HttpServletRequest request, DataSessionPivot ds);

}
