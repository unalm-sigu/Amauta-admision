package pe.edu.lamolina.pivot.controller.academico.ciclo;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("ciclo")
public class CicloAcademicoController {

    @Autowired
    CicloAcademicoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "academico/ciclo/ciclo";
    }

    @RequestMapping("changeciclo")
    public String changeciclo(HttpSession session, Model model) {
        List<CicloAcademico> ciclos = service.allCicloAcademico(4);
        model.addAttribute("cicloAcademicos", ciclos);
        return "academico/ciclo/cicloland";
    }

    @ResponseBody
    @RequestMapping(value = "cicloland", method = RequestMethod.POST)
    public void cicloland(HttpSession session, @RequestParam("ciclo") Long ciclo) throws Exception {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = service.getCicloAcademico(ciclo);
        ds.setCicloAcademico(cicloAcademico);
        session.setAttribute(Constantine.SESSION_USUARIO, ds);
        
    }

}
