package pe.edu.lamolina.amauta.controller.escalafon.distincion;

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
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Controller
@RequestMapping("escalafon/distincion")
public class DistincionController {

    @Autowired
    DistincionService service;

    @ResponseBody
    @RequestMapping("loadListDistincionEscalafon")
    public JsonResponse loadListDistincionEscalafon(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<DistincionEscalafon> listDistincionEscalafon = service.allDistincionByEscalafon(escalafon);
            for (DistincionEscalafon item : listDistincionEscalafon) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "*", "pais.*"
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
    public JsonResponse save(@RequestBody DistincionEscalafon distincionEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.save(distincionEscalafon);
            response.setSuccess(true);
            if (distincionEscalafon.getId() != null) {
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
    public JsonResponse eliminar(@RequestBody DistincionEscalafon distincionEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(distincionEscalafon);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
