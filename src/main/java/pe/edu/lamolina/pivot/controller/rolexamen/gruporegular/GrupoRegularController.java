package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/gruporegular")
public class GrupoRegularController {

    @Autowired
    GrupoRegularService grupoRegularService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return "rolexamen/gruporegular/grupoRegular";
    }

    @RequestMapping(value = "nuevogruporegular", method = RequestMethod.GET)
    public String nuevoGrupoRegular(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = grupoRegularService.allRolExamenesActives(ds.getCicloAcademico());

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
        /*
        GrupoRegularExamen grupoRegularExamen = new GrupoRegularExamen();
        grupoRegularExamen.setRolExamen(new RolExamenes());
        model.addAttribute("jGrupoRegularExamen",
                JsonHelper.createJson(grupoRegularExamen, jc, true,
                        new String[]{
                            "*",
                            "rolExamen.*"}).toString()
        );*/
        return "rolexamen/gruporegular/nuevoGrupoRegular";
    }

    @ResponseBody
    @RequestMapping(value = "calcularGruposRegulares", method = RequestMethod.POST)
    public JsonResponse calcularGruposRegulares(@RequestBody GrupoRegularExamen grupoRegularExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            grupoRegularService.calcularExamenesGrupoRegular(grupoRegularExamen, ds.getCicloAcademico());
            response.setMessage("Grupos regulares calculados corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
