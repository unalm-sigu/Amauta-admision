package pe.edu.lamolina.pivot.controller.restcontroller;

import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.pivot.zelper.bean.FormImport;

public interface RestPivotService {

    Boolean validateToken(FormImport json);

    TokenIngresante findToken(String token, Long idUsuario);

}
