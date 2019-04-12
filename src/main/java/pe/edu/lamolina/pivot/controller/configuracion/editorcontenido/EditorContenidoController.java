package pe.edu.lamolina.pivot.controller.configuracion.editorcontenido;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("configuracion/editorcontenido")
public class EditorContenidoController {

    @Autowired
    EditorContenidoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        logger.debug("inicio");
        return "configuracion/editorContenido/contenidos";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            List<ContenidoCarta> contenidos = service.allContenidoCartaByDynaTable(filter);
            logger.debug("SIZE DE CONTENIDOS {}", contenidos.size());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ContenidoCarta contenido : contenidos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", contenido.getId());
                node.put("nombre", contenido.getNombre());
                node.put("contenido", contenido.getContenido());
                node.put("codigo", contenido.getCodigo());
                node.put("tipo", contenido.getTipo());
                node.put("tipoEnum", contenido.getTipoEnum().getValue());
                node.put("imgUrl", contenido.getImgUrl());
                node.put("sistema", contenido.getSistema().getNombre());

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

    @RequestMapping("{idContenido}/update")
    public String editarContenido(@PathVariable("idContenido") Long idContenido,
            Model model, HttpSession session) {

        ContenidoCarta contenido = service.findContenidoCartaById(idContenido);
        List<ContenidoCartaVariable> variablesCarta = service.allVariablesCartaByContenido(idContenido);
        List<ContenidoVariable> variables = service.allVariables();
        List<Sistema> sistemas = service.allSistema();

        ArrayNode sistemasJson = new ArrayNode(JsonNodeFactory.instance);
        for (Sistema sis : sistemas) {
            ObjectNode itemJson = JsonHelper.createJson(sis, JsonNodeFactory.instance, true, new String[]{"*"});
            sistemasJson.add(itemJson);
        }

        ArrayNode variablesJson = new ArrayNode(JsonNodeFactory.instance);
        for (ContenidoVariable var : variables) {
            ObjectNode itemJson = JsonHelper.createJson(var, JsonNodeFactory.instance, true, new String[]{"*"});
            variablesJson.add(itemJson);
        }

        ArrayNode variablesCartaJson = new ArrayNode(JsonNodeFactory.instance);
        for (ContenidoCartaVariable var : variablesCarta) {
            ObjectNode itemJson = JsonHelper.createJson(var, JsonNodeFactory.instance, true, new String[]{
                "*", "contenidoVariable.*", "contenidoCarta.id"
            });
            variablesCartaJson.add(itemJson);
        }

        ObjectNode contenidoJson = JsonHelper.createJson(contenido, JsonNodeFactory.instance, true, new String[]{
            "*", "sistema.*"
        });

        model.addAttribute("contenidoJson", contenidoJson.toString());
        model.addAttribute("variablesCartaJson", variablesCartaJson.toString());
        model.addAttribute("variablesJson", variablesJson.toString());
        model.addAttribute("sistemasJson", sistemasJson.toString());

        return "configuracion/editorContenido/contenido";
    }

    @RequestMapping("{idContenido}/ver")
    public String ver(@PathVariable("idContenido") Long idContenido,
            Model model, HttpServletResponse response, HttpSession session) {

        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        ContenidoCarta contenido = service.findSoloContenidoCartaById(idContenido);
        model.addAttribute("contenido", contenido);

        return "configuracion/editorContenido/verContenido";
    }

    @ResponseBody
    @RequestMapping("updateContenido")
    public JsonResponse updateContenido(
            @RequestParam("idContenido") Long idContenido,
            @RequestParam("contenido") String contenido,
            @RequestParam("idSistema") Long idSistema,
            HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.updateContenido(idContenido, contenido, idSistema);

            response.setMessage("Contenido Actualizado");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateImg")
    public JsonResponse updateImg(@RequestParam("idContenido") Long idContenido,
            @RequestParam("fileName") String fileName, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            service.updateImgUrl(idContenido, fileName);

            response.setMessage("Contenido Actualizado");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevo(HttpSession session) {

        return "configuracion/editorContenido/contenidoModal";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(ContenidoCarta contenido, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            if (contenido.getId() != null) {
                service.save(contenido);
                response.setMessage("Taller Actualizado");
            } else {
                service.save(contenido);
                response.setMessage("Se Agregó un Nuevo Taller");
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
    @RequestMapping("{idContenido}/addVariable")
    public JsonResponse addVariable(
            @PathVariable("idContenido") Long idContenido,
            @RequestBody ContenidoCartaVariable contVariable, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.addVariable(contVariable, idContenido);
            response.setMessage("Variable agregada satisfctoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idVariable}/deleteVariable")
    public JsonResponse deleteVariable(
            @PathVariable("idVariable") Long idContVariable, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.deleteVariable(idContVariable);
            response.setMessage("Variable eliminada satisfctoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateVariable")
    public JsonResponse updateContVariable(@RequestBody ContenidoCartaVariable contVariable, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            service.updateContVariable(contVariable);
            response.setMessage("Variable actualizada satisfctoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idContenido}/allVariables")
    public JsonResponse allVariables(
            @PathVariable("idContenido") Long idContenido, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<ContenidoCartaVariable> variablesCarta = service.allVariablesCartaByContenido(idContenido);
            ArrayNode variablesCartaJson = new ArrayNode(JsonNodeFactory.instance);
            for (ContenidoCartaVariable var : variablesCarta) {
                ObjectNode itemJson = JsonHelper.createJson(var, JsonNodeFactory.instance, true, new String[]{
                    "*", "contenidoVariable.*", "contenidoCarta.id"
                });
                variablesCartaJson.add(itemJson);
            }

            response.setData(variablesCartaJson);
            response.setMessage("Variable agregada satisfctoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }
}
