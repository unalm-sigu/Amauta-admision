package pe.edu.lamolina.amauta.controller.escalafon.experiencia;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;

@Controller
@RequestMapping("escalafon/experiencia")
public class ExperienciaController {

    @Autowired
    ExperienciaService service;

    @ResponseBody
    @RequestMapping("loadListExperienciaEscalafon")
    public JsonResponse loadListExperienciaEscalafon(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<ExperienciaEscalafon> listAcademicoEscalfon = service.allExperienciaByEscalafon(escalafon);
            for (ExperienciaEscalafon item : listAcademicoEscalfon) {
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
    public JsonResponse save(@RequestBody ExperienciaEscalafon experienciaEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.save(experienciaEscalafon);
            response.setSuccess(true);
            if (experienciaEscalafon.getId() != null) {
                response.setMessage("El registro fue actualizado satisfactoriamente");
            } else {
                response.setMessage("Se registro fue creado satisfactoriamente");
            }
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody ExperienciaEscalafon experienciaEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(experienciaEscalafon);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
