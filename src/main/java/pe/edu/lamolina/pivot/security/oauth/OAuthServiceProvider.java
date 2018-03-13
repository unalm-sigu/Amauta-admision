package pe.edu.lamolina.pivot.security.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.scribe.oauth.OAuthService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OAuthServiceProvider {

    OAuthService getService();

    void loginManually(String email, HttpSession session, HttpServletRequest servlet);

    void createLogJson(DataSessionPivot ds, HttpSession session);

    void createLogJsonLogout(DataSessionPivot ds, HttpSession session);

}
