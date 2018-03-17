package pe.edu.lamolina.pivot.controller.academico.encuesta.respuesta;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuesta/respuesta")
public class RespuestaEncuestaController {

    @Autowired
    RespuestaEncuestaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloPostula ciclo = new CicloPostula();

        ExamenVirtual encuesta = service.findEncuestaActivaByCiclo(ciclo);
        List<PreguntaExamen> preguntas = service.allPreguntasOtros(encuesta);

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("preguntas", preguntas);

        return "academico/encuesta/respuesta/respuesta";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloPostula ciclo = new CicloPostula();

            List<RespuestaItem> respuestasOtro = service.allResumenRespuestasOtro(filter, ciclo);
            filter.setTotal(respuestasOtro.size());
            filter.setFiltered(respuestasOtro.size());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (RespuestaItem otro : respuestasOtro) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("contenido", otro.getContenido());
                node.put("cantidad", otro.getCantidad());
                array.add(node);
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
    @RequestMapping("allOpciones")
    public JsonResponse allOpciones(@RequestParam("id") Long idPregunta, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            PreguntaExamen pregunta = service.findPregunta(idPregunta);
            List<OpcionPregunta> opciones = service.allOpcionesOtrosByPregunta(pregunta);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (OpcionPregunta opcion : opciones) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", opcion.getId());
                json.put("text", opcion.getLetra() + ") " + opcion.getContenido());
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("unirFrases")
    public JsonResponse unirFrases(OpcionPregunta opcion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloPostula ciclo = new CicloPostula();

            service.unirFrases(opcion, ciclo);
            response.setSuccess(true);
            response.setMessage("Se han unificado satisfactoriamente las dos frases");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("modificarFrase")
    public JsonResponse modificarFrase(OpcionPregunta opcion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloPostula ciclo = new CicloPostula();

            service.modificarFrase(opcion, ciclo);
            response.setSuccess(true);
            response.setMessage("Se modificó la frase satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
