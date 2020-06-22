package pe.edu.lamolina.amauta.controller.escalafon.investigacion;

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
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

@Controller
@RequestMapping("escalafon/investigacion")
public class InvestigacionController {

    @Autowired
    InvestigacionService service;

    @ResponseBody
    @RequestMapping("loadListInvestigacionEscalafon")
    public JsonResponse loadListInvestigacionEscalafon(@RequestBody Escalafon escalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        try {
            List<InvestigacionEscalafon> listExperienciaAsesor = service.allInvestigacionEscalafonByEscalafon(escalafon);
            for (InvestigacionEscalafon item : listExperienciaAsesor) {
                array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "*", "area.*"
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
    public JsonResponse save(@RequestBody InvestigacionEscalafon investigacionEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.save(investigacionEscalafon);
            response.setSuccess(true);
            if (investigacionEscalafon.getId() != null) {
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
    public JsonResponse eliminar(@RequestBody InvestigacionEscalafon investigacionEscalafon, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(investigacionEscalafon);
            response.setMessage("El registro fue eliminado satisfactoriamente");
            response.setSuccess(true);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
