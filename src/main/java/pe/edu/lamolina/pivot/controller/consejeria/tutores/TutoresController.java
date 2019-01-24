package pe.edu.lamolina.pivot.controller.consejeria.tutores;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/tutores")
public class TutoresController {

//    @Autowired
//    TutoresService service;
//
//    @RequestMapping(method = RequestMethod.GET)
//    public String index(Model model, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
//
//        return "consejeria/tutores";
//    }
//
//    @ResponseBody
//    @RequestMapping("list")
//    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
//
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
//
//        //List<Docente> docentes = service.allByDynatableFilter(filter);
//        return null;
//
//    }

}
