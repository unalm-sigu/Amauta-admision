package pe.edu.lamolina.pivot.controller.encuesta.editor;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/encuesta/editor")
public class EditorEncuestaController {

    @Autowired
    EditorEncuestaService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        CicloPostula ciclo = service.findCicloActivo();
        model.addAttribute("ciclo", ciclo);
        return "academico/encuesta/editor/encuestaEditor";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            List<ExamenVirtual> encuestas = service.allEncuesta(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ExamenVirtual encuesta : encuestas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", encuesta.getId());
                node.put("nombre", encuesta.getNombre());
                node.put("estado", encuesta.getEstado());
                node.put("estadoEnum", encuesta.getEstadoEnum().getValue());
                node.put("codigo", encuesta.getCodigo());
                node.put("preguntasDisponibles", encuesta.getPreguntasDisponibles());
                node.put("preguntasVisibles", encuesta.getPreguntasVisibles());
                node.put("cicloInicio", (String) ObjectUtil.getParentTree(encuesta, "cicloInicio.cicloAcademico.descripcion"));
                node.put("cicloFin", (String) ObjectUtil.getParentTree(encuesta, "cicloFin.cicloAcademico.descripcion"));
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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        model.addAttribute("encuesta", new ExamenVirtual());
        return "academico/encuesta/editor/encuestaEditorForm";
    }

    @RequestMapping("{encuesta}/update")
    public String update(@PathVariable("encuesta") Long idEncuesta, Model model, HttpSession session) {
        ExamenVirtual evaluacionVirtual = service.findEncuesta(idEncuesta);
        model.addAttribute("encuesta", evaluacionVirtual);
        return "academico/encuesta/editor/encuestaEditorForm";
    }

    @RequestMapping("{encuesta}/preview")
    public String preview(@PathVariable("encuesta") Long idEncuesta, Model model, HttpSession session) {
        CicloPostula ciclo = service.findCicloActivo();
        ExamenVirtual encuesta = service.findEncuesta(idEncuesta);
        List<PreguntaExamen> preguntas = service.allPreguntasByEncuesta(encuesta);

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("encuesta", encuesta);
        model.addAttribute("ciclo", ciclo);

        return "academico/encuesta/preview/encuestaPreview";
    }

    @RequestMapping("save")
    public String save(ExamenVirtual encuesta, RedirectAttributes redirectAttr, Model model, HttpSession session) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (encuesta.getId() == null) {
                service.saveEncuesta(encuesta, ds);
                Notificaciones.crearMsg("Registro creado", redirectAttr);
            } else {
                service.updateEncuesta(encuesta);
                Notificaciones.crearMsg("Registro actualizado", redirectAttr);
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
        }

        return "redirect:/encuesta/editor";
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            service.delete(encuesta);
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
    @RequestMapping("duplicar")
    public JsonResponse duplicar(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.duplicar(encuesta, ds);
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
    public JsonResponse estado(ExamenVirtual encuesta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambiarEstadoEncuesta(encuesta, ds);
            response.setMessage("Registro actualizado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }
}
