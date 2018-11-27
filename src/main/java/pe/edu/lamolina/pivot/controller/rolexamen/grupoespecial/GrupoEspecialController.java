package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/grupoespecial")
public class GrupoEspecialController {

    @Autowired
    GrupoEspecialService grupoEspecialService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = grupoEspecialService.allRolExamenesActives(ds.getCicloAcademico());
        JsonNodeFactory jc = JsonNodeFactory.instance;

        ArrayNode jRolesExamenes = new ArrayNode(jc);
        rolesExamenes.forEach(x -> {
            jRolesExamenes.add(JsonHelper.createJson(x, jc, false,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.eventoAcademico.*"
                    }));
        });
        model.addAttribute("jRolesExamenes", jRolesExamenes.toString());

        return "rolexamen/grupoespecial/grupoEspecial";
    }

}
