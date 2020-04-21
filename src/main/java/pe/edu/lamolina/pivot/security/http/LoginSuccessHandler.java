package pe.edu.lamolina.pivot.security.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authenticated) throws IOException, ServletException {

        DataSessionPivot dataSession = new DataSessionPivot();
        request.getSession().setAttribute(GlobalConstantine.SESSION_USUARIO, dataSession);
        setDefaultTargetUrl("/adm");
        super.onAuthenticationSuccess(request, response, authenticated);
    }
}
