package pe.edu.lamolina.pivot.controller.consejeria.consejero;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/aconsejadosdocente")
public class AconsejadosDocenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AconsejadosDocentesService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{
            "*"
        }));
        return "consejeria/consejero/consejero";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List< AlumnoConsejero> consejeros = service.allByDynatable(filter, ds.getCicloAcademico(), ds.getPersona());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoConsejero consjros : consejeros) {
                ObjectNode node = JsonHelper.createJson(consjros, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "alumno.*",
                            "alumno.cicloIngreso.*",
                            "alumno.situacionAcademica.*",
                            "alumno.persona.*",
                            "alumno.persona.tipoDocumento.*",
                            "alumno.persona.tipoDocumento.*",
                            "consejero.*",
                            "consejero.colaborador.persona.*",
                            "consejero.colaborador.persona.tipoDocumento.*",});

                array.add(node);
            }
            json.setFiltered(filter.getFiltered());
            json.setData(array);
            json.setTotal(filter.getTotal());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("countData")
    public JsonResponse countData( HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersona(ds.getPersona(), ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            json.setData(JsonHelper.createJson(aconsejadoEstadoBean, JsonNodeFactory.instance, new String[]{"*"}));
            json.setMessage("Búsqueda Exitosa");

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
