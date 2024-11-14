package pe.edu.lamolina.amauta.controller.docente.notasacademicas.tipoevaluacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("tipoevaluacion")
public class TipoEvaluacionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoEvalucionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//        List<TipoEvaluacion> tipoEvaluacionList = service.allTiposEvaluacion();
//
//        ArrayNode arrayTipoBeca = JaneHelper.from(tipoEvaluacionList).array();
//        model.addAttribute("tiposEvaluacion", arrayTipoBeca);
        return "docente/notaacademica/tipoevaluacion/tipoevaluacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpServletRequest session) {
        DynatableResponse response = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try{
            List<TipoEvaluacion> tipoEvaluacion = service.allByDynatable(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (TipoEvaluacion t : tipoEvaluacion) {
                ObjectNode node = JsonHelper.createJson(t, JsonNodeFactory.instance, true,
                        new String[]{
                    "id","nombre","codigo","orden"
                });
                array.add(node);
            }

            response.setData(array);
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());

        }catch (Exception e){
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TipoEvaluacion tipo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(tipo);
            response.setMessage("Se guardo el anexo satisfactoriamente");
            response.setSuccess(true);
        }catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value="update", method = RequestMethod.POST)
    public JsonResponse updateBeca(@RequestBody TipoEvaluacion tipo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        json.setSuccess(false);
        try {
            service.actualizarTipoEvaluacion(tipo);
            json.setSuccess(true);
            json.setMessage(GlobalMessages.UPDATED);
        }catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }
}
