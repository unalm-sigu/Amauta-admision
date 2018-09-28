package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.edu.lamolina.model.session.DataSessionMaipi;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Controller
@RequestMapping("posgrado/cuotasalumno")
public class CuotasAlumnoController {

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionMaipi ds = (DataSessionMaipi) session.getAttribute(Constantine.SESSION_USUARIO);

        return "posgrado/cuotasalumno/cuotasAlumno";
    }

}
