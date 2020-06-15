package pe.edu.lamolina.amauta.controller.escalafon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("escalafon")
public class EscalafonController {

    @RequestMapping(method = RequestMethod.GET)
    public String index() {

        return "escalafon/lista";
    }

    @RequestMapping("update/{idEscalafon}")
    public String editor(@PathVariable Long idEscalafon) {

        return "escalafon/editorEscalafon";
    }

}
