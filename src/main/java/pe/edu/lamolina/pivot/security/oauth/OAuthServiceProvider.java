package pe.edu.lamolina.pivot.security.oauth;

import javax.servlet.http.HttpSession;
import org.scribe.oauth.OAuthService;

public interface OAuthServiceProvider {

    OAuthService getService();

    void loginManually(String email, HttpSession session);
}
