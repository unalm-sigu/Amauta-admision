package pe.edu.lamolina.amauta.controller.general.producto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("general/aula/producto/resumen")
public class ProductoResumenController {
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "general/aula/producto/resumen";
    }
}
