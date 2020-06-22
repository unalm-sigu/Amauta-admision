package pe.edu.lamolina.amauta.controller.escalafon.Idioma;

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
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

@Controller
@RequestMapping("escalafon/idioma")
public class IdiomaEscalafonController {

    @Autowired
    IdiomaEscalafonService service;

    @ResponseBody
    @RequestMapping("loadListIdiomaEscalafon")
    public JsonResponse loadListIdiomaEscalafon(@RequestBody Escalafon Escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<IdiomaEscalafon> listIdiomaEscalafon = service.allIdiomaEscalafonByEscalafon(Escalafon);
            for (IdiomaEscalafon item : listIdiomaEscalafon) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "*", "idioma.*", "escalafon.id"
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
    public JsonResponse save(@RequestBody IdiomaEscalafon idiomaEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.save(idiomaEscalafon);
            response.setSuccess(true);
            if (idiomaEscalafon.getId() != null) {
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
    public JsonResponse eliminar(@RequestBody IdiomaEscalafon idiomaEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(idiomaEscalafon);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
