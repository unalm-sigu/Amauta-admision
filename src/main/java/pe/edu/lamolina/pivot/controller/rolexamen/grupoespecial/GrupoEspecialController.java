package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
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

    @ResponseBody
    @RequestMapping(value = "listGruposEspeciales", method = RequestMethod.GET)
    public DynatableResponse listGruposEspeciales(DynatableFilter filter, @RequestParam("rolexamenes") Long idRolExamenes, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<SeccionGrupoEspecial> list = grupoEspecialService.allSeccionesGrupoEspecialByRolExamenes(filter, new RolExamenes(idRolExamenes));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (SeccionGrupoEspecial item : list) {
            ObjectNode jItem = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "seccion.*",
                "rolExamenes.*",
                "userRegistro.*",
                "userRegistro.persona.*"
            });
            jItem.put("alumnosEspecialesActivosCount", item.getAlumnosEspecialesActivosCount());
            array.add(jItem);
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

}
