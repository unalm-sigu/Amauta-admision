package pe.edu.lamolina.amauta.controller.restcontroller;

import java.util.List;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.amauta.zelper.bean.FormImport;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface RestPivotService {

    Boolean consumirToken(FormImport form);

    Boolean verificarToken(FormImport form);

    Usuario getUsuario(Usuario usuario);

    TokenIngresante findToken(String token, Long idUsuario);

    CicloAcademico findCicloActivoPregrado();

    List<CicloAcademico> allCiclos();

}
