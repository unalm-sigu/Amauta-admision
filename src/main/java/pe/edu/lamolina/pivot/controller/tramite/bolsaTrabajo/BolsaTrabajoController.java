package pe.edu.lamolina.pivot.controller.tramite.bolsaTrabajo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/bolsatrabajo")
public class BolsaTrabajoController {

    @Autowired
    BolsaTrabajoService service;

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
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ObjectNode obj = JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{
            "id",
            "descripcion"
        });
        model.addAttribute("cicloacademico", obj);
        return "tramite/bolsatrabajo/bolsaTrabajo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse response = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteSubvencion> tramiteSubv = service.allTramiteSubvByColabo(ds.getPersona(), ds.getCicloAcademico());
            for (TramiteSubvencion tramiteSubvencion : tramiteSubv) {
                ObjectNode obj = JsonHelper.createJson(tramiteSubvencion, JsonNodeFactory.instance, true, new String[]{
                    "*",
                    "tipoSubvencion.id",
                    "tipoSubvencion.nombre",
                    "tramite.id",
                    "tramite.serie",
                    "tramite.numero",
                    "tramite.tipoTramite.id",
                    "tramite.tipoTramite.nombre",
                    "tramite.alumno",
                    "tramite.alumno.id",
                    "tramite.alumno.codigo",
                    "tramite.alumno.persona.id",
                    "tramite.alumno.persona.nombreCompleto",
                    "tramite.alumno.persona.numeroDocIdentidad",
                    "tramite.alumno.persona.rutaFoto",
                    "tramite.alumno.persona.tipoFoto",
                    "tramite.alumno.persona.tipoDocumento.id",
                    "tramite.alumno.persona.tipoDocumento.simbolo",
                    "tramite.alumno.carrera.id",
                    "tramite.alumno.carrera.nombre",
                    "tramite.alumno.carrera.facultad.id",
                    "tramite.alumno.carrera.facultad.nombre",
                    "tramite.alumno.carrera.facultad.simbolo",});
                arrayNode.add(obj);
            }
            response.setData(arrayNode);

            response.setTotal(arrayNode.size());
            response.setFiltered(arrayNode.size());
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TramiteSubvencion tramiteSubvencion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.updateTramiteSubvencion(tramiteSubvencion, ds.getUsuario());
            response.setSuccess(true);
            response.setMessage("Se actualizó el trámite");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
