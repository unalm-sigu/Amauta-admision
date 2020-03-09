package pe.edu.lamolina.pivot.controller.restcontroller;

import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.bean.FormImport;

public interface RestPivotService {

    Boolean consumirToken(FormImport form);

    Boolean verificarToken(FormImport form);

    Usuario getUsuario(Usuario usuario);

}
