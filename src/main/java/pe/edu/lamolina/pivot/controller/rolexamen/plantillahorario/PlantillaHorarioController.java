package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/plantillahorario")
public class PlantillaHorarioController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlantillaHorarioService plantillaHorarioService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = plantillaHorarioService.allRolExamenesActives(ds.getCicloAcademico());
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

        return "rolexamen/plantillahorario/plantillaHorario";
    }

    @ResponseBody
    @RequestMapping(value = "changeRolExamen", method = RequestMethod.POST)
    public JsonResponse changeRolExamen(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            rolExamenes = plantillaHorarioService.findRolExamenes(rolExamenes);
            response.setData(JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.eventoAcademico.*",
                        "semanasExamen.rolExamenes",
                        "semanasExamen.*",
                        "semanasExamen.horaFin",
                        "semanasExamen.horaInicio"
                    }));

            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "changeSemanaExamen", method = RequestMethod.POST)
    public JsonResponse changeSemanaExamen(@RequestBody SemanaExamen semanaExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            logger.debug("changeSemanaExamen");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "calcularPlantillaHorario", method = RequestMethod.POST)
    public JsonResponse calcularPlantillaHorario(@RequestBody SemanaExamen semanaExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            logger.debug("changeSemanaExamen");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
