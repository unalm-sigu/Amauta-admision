package pe.edu.lamolina.amauta.controller.programacionhorarios.boletinacademico;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("boletin/reporte")
public class BoletinAcademicoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoletinAcademicoService service;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.reporteAnexoBoletin(ds);
        return "funko";
    }

}
