package pe.edu.lamolina.pivot.controller.general.tipocarpeta;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
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
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/tipocarpeta")
public class TipoCarpetaController {

    @Autowired
    TipoCarpetaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        return "general/tipocarpeta/tipocarpeta";
    }

    @ResponseBody
    @RequestMapping("allTipoCarpeta")
    public JsonResponse allTipoCarpeta(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<TipoCarpeta> tipoCarpetas = service.allTipoCarpeta(ds);

            JsonNodeFactory factory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(factory);

            for (TipoCarpeta tipoCarpeta : tipoCarpetas) {
                ObjectNode json = JsonHelper.createJson(tipoCarpeta, factory, true, new String[]{
                    "*",
                    "tipoCarpetaPadre",
                    "tipoCarpetaSuperior.*"
                });
                array.add(json);
            }

            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TipoCarpeta tipoCarpeta, HttpSession session) {
        JsonResponse response = new JsonResponse();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            service.save(tipoCarpeta, ds);
            response.setMessage("Se realizó la operación satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
