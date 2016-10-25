package pe.edu.lamolina.pivot.security.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authenticated) throws IOException, ServletException {

        DataSession dataSession = new DataSession();
        request.getSession().setAttribute(Constantine.SESSION_USUARIO, dataSession);
        setDefaultTargetUrl("/adm");
        super.onAuthenticationSuccess(request, response, authenticated);
    }
}
