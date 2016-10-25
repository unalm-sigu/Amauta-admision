package pe.edu.lamolina.pivot.security.http;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String index(HttpSession session) {

        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        if (ds != null) {
            return "redirect:/test";
        }

        return "security/login";
    }

}
