package pe.edu.lamolina.pivot.controller.encuesta.pregunta;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuesta/editor/pregunta")
public class PreguntaEncuestaController {

    @Autowired
    PreguntaEncuestaService service;

    @Autowired
    SpringTemplateEngine springHtml;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET, path = "{encuesta}")
    public String index(Model model, @PathVariable("encuesta") Long idEncuesta, HttpSession session) {
        ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
        model.addAttribute("encuesta", encuesta);
        return "academico/encuesta/pregunta/pregunta";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, Long idEncuesta, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
            PreguntaExamen preguntaTop = service.findPreguntaNumeroTop(idEncuesta);
            List<PreguntaExamen> preguntas = service.allPreguntaEvaluacionVirtual(filter, encuesta);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (PreguntaExamen pregunta : preguntas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", pregunta.getId());
                node.put("numero", pregunta.getNumero());
                node.put("tipo", pregunta.getTipo());
                node.put("texto", pregunta.getTexto());
                node.put("estado", pregunta.getEstado());
                node.put("estadoEncuesta", encuesta.getEstado());
                node.put("estadoEnum", pregunta.getEstadoEnum().getValue());
                node.put("max", pregunta.getNumero() == preguntaTop.getNumero().intValue());
                node.put("opciones", pregunta.getOpcionPregunta().size());

                if (pregunta.getOpcionReferencia() != null) {
                    OpcionPregunta opcionRef = pregunta.getOpcionReferencia();
                    String referencias = "Pregunta " + opcionRef.getPregunta().getNumero();
                    referencias += " opción " + opcionRef.getLetra();
                    node.put("referencias", referencias);
                }

                List<OpcionPregunta> opciones = pregunta.getOpcionPregunta();
                ArrayNode arrayOpciones = new ArrayNode(JsonNodeFactory.instance);
                for (OpcionPregunta opcion : opciones) {
                    ObjectNode nodeOpcion = new ObjectNode(JsonNodeFactory.instance);
                    nodeOpcion.put("contenido", opcion.getContenido());
                    nodeOpcion.put("letra", opcion.getLetra());
                    nodeOpcion.put("esOtro", opcion.getEsOtro());

                    arrayOpciones.add(nodeOpcion);
                }

                node.put("opcionPregunta", arrayOpciones);
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

    @RequestMapping("{encuesta}/nuevo")
    public String nuevo(@PathVariable("encuesta") Long idEncuesta, Model model, HttpSession session) {
        ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
        PreguntaExamen pregunta = new PreguntaExamen();
        pregunta.setExamenVirtual(encuesta);

        model.addAttribute("tipos", TipoPreguntaEncuestaEnum.values());
        model.addAttribute("pregunta", pregunta);
        return "academico/encuesta/pregunta/preguntaForm";
    }

    @RequestMapping("{pregunta}/update")
    public String update(@PathVariable("pregunta") Long idPregunta, Model model, HttpSession session) {
        PreguntaExamen pregunta = service.findPregunta(idPregunta);

        model.addAttribute("tipos", TipoPreguntaEncuestaEnum.values());
        model.addAttribute("pregunta", pregunta);
        return "academico/encuesta/pregunta/preguntaForm";
    }

    @RequestMapping("save")
    public String save(PreguntaExamen pregunta, RedirectAttributes redirectAttr, Model model, HttpSession session) {
        ExamenVirtual encuesta = pregunta.getExamenVirtual();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (pregunta.getId() == null) {
                service.savePregunta(pregunta, ds);
                Notificaciones.crearMsg("Registro creado", redirectAttr);
            } else {
                service.updatePregunta(pregunta, ds);
                Notificaciones.crearMsg("Registro actualizado", redirectAttr);
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }

        return "redirect:/academico/encuesta/editor/pregunta/" + encuesta.getId();
    }

    @ResponseBody
    @RequestMapping("allReferencia")
    public JsonResponse allReferencia(PreguntaExamen pregunta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<PreguntaExamen> preguntas = service.allReferencia(pregunta);
            Context ctx = new Context();
            ctx.setVariable("preguntas", preguntas);
            String htmlContent = springHtml.process("encuesta/pregunta/referencias", ctx);
            response.setData(htmlContent);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(PreguntaExamen pregunta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.deletePregunta(pregunta);
            response.setMessage("Registro eliminado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(PreguntaExamen pregunta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarEstadoPregunta(pregunta, ds);
            response.setMessage("Se actualizó el estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("allOpcionReferencia")
    public JsonResponse allOpcionReferencia(
            @RequestParam("nombre") String nombre,
            @RequestParam("encuesta") Long encuesta,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            ExamenVirtual evaluacionVirtual = new ExamenVirtual(encuesta);

            List<OpcionPregunta> opciones = service.allOpcionesByName(nombre, evaluacionVirtual);
            ArrayNode array = new ArrayNode(jsonFactory);

            for (OpcionPregunta opcion : opciones) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", opcion.getId());
                a.put("codigo", opcion.getLetra());
                a.put("nombre", opcion.getContenido());
                a.put("referenciaNumero", opcion.getPregunta().getNumero());
                a.put("referenciaTexto", opcion.getPregunta().getTexto());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("sort")
    public JsonResponse sort(PreguntaExamen pregunta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            service.upateNumeroPregunta(pregunta);
            response.setMessage("Se modificó el número de pregunta satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
