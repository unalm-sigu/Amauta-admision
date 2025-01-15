package pe.edu.lamolina.amauta.security.oauth;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface OAuthServiceProvider {

    void loginManually(String email, HttpSession session, HttpServletRequest servlet);

    void createLogJson(DataSessionPivot ds, HttpSession session);

    void createLogJsonLogout(DataSessionPivot ds, HttpSession session);

    void asignarRolActivo(Rol asignar, DataSessionPivot ds, HttpSession session);

    List<CicloAcademico> findCiclosVisibles();

}
