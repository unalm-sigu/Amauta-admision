package pe.edu.lamolina.pivot.security.oauth;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.scribe.oauth.OAuthService;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OAuthServiceProvider {

    OAuthService getService();

    void loginManually(String email, HttpSession session, HttpServletRequest servlet);

    void createLogJson(DataSessionPivot ds, HttpSession session);

    void createLogJsonLogout(DataSessionPivot ds, HttpSession session);

    List<Menu> allMenuRolActivo(Rol rolAsignar, Sistema sistema);

}
