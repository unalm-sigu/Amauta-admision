package pe.edu.lamolina.pivot.controller.mensajeria.tipomsgintranet;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.enums.TipoMensajeIntranetEnum;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("mensajeria/tipomsgintranet")
public class TipoMsgIntranetController {

    @Autowired
    TipoMsgIntranetService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        ObjectNode cicloJSON = new ObjectNode(JsonNodeFactory.instance);
        cicloJSON.put("descripcion", ds.getCicloAcademico().getDescripcion());
        ArrayNode enums = new ArrayNode(JsonNodeFactory.instance);
        for (TipoMensajeIntranetEnum msjEnum : TipoMensajeIntranetEnum.values()) {
            ObjectNode enumObj = new ObjectNode(JsonNodeFactory.instance);
            enumObj.put("value", msjEnum.getValue());
            enumObj.put("name", msjEnum.name());
            enums.add(enumObj);
        }

        model.addAttribute("ciclo", cicloJSON);
        model.addAttribute("TipoMensajeIntranetEnums", enums);

        return "mensaje/tipoMsgIntranet/tipoMsgIntranet";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<TipoMensajeIntranet> tiposMsg = service.allByDynatble(filter);
            for (TipoMensajeIntranet tipoMsg : tiposMsg) {
                ObjectNode obj = JsonHelper.createJson(tipoMsg, JsonNodeFactory.instance, new String[]{
                    "*"
                });
                array.add(obj);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping("saveUpdate")
    public JsonResponse saveUpdate(@RequestBody TipoMensajeIntranet tipoMsg, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            if (tipoMsg.getId() == null) {
                service.save(tipoMsg, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(GlobalMessages.CREATED);
            } else {
                service.update(tipoMsg, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(GlobalMessages.UPDATED);
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody TipoMensajeIntranet tipoMsg, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            service.eliminar(tipoMsg);
            response.setMessage(GlobalMessages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
