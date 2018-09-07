package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface VerificadorService {

    void revisarPermiso(HttpServletRequest request, DataSessionPivot ds);

    public <T> List<T> allInstanciasByMenuRol(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds);
    

}
