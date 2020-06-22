package pe.edu.lamolina.amauta.controller.escalafon.experienciaAsesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

@Controller
@RequestMapping("escalafon/experienciaAsesor")
public class ExperienciaAsesorController {

    @Autowired
    ExperienciaAsesorService service;

    @ResponseBody
    @RequestMapping("loadListExperienciaAsesor")
    public JsonResponse loadListExperienciaAsesor(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<ExperienciaAsesor> listExperienciaAsesor = service.allExperienciaAsesorByEscalafon(escalafon);
            for (ExperienciaAsesor item : listExperienciaAsesor) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "*", "universidad.*"
                }));
            }
            response.setData(array);
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody ExperienciaAsesor experienciaAsesor, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.save(experienciaAsesor);
            response.setSuccess(true);
            if (experienciaAsesor.getId() != null) {
                response.setMessage("El registro fue actualizado satisfactoriamente");
            } else {
                response.setMessage("Se registro fue creado satisfactoriamente");
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody ExperienciaAsesor experienciaAsesor, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(experienciaAsesor);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
