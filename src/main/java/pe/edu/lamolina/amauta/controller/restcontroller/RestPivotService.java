package pe.edu.lamolina.amauta.controller.restcontroller;

import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.amauta.zelper.bean.FormImport;

public interface RestPivotService {

    Boolean consumirToken(FormImport form);

    Boolean verificarToken(FormImport form);

    Usuario getUsuario(Usuario usuario);

    TokenIngresante findToken(String token, Long idUsuario);

}
