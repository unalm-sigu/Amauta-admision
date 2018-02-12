package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/configuracionturno")
public class ConfiguracionTurnoController {

    @Autowired
    ConfiguracionMatriculaService service;

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
    public String index(Model model, HttpSession session) throws ParseException {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<EventoAcademico> evento = service.findEventoCiclo(ds.getCicloAcademico());
        List<ConfiguracionTurnosAtencion> configuraciones = service.allConfiguraciones(ds.getCicloAcademico());

        ArrayNode lstEventos = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstConfig = new ArrayNode(JsonNodeFactory.instance);
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode objNodeConfig = new ObjectNode(JsonNodeFactory.instance);

        System.err.println("conf    " + configuraciones.size());
        model.addAttribute("eventos", new EventoAcademico().toJsonArray(evento));
        model.addAttribute("configuraciones", new ConfiguracionTurnosAtencion().toJsonArray(configuraciones));
        model.addAttribute("ciclo", ds.getCicloAcademico().getDescripcion2());

        return "academico/matricula/matriculaConfiguracion";
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public JsonResponse list(@RequestBody ConfiguracionTurnosAtencion config, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DateFormat formatter = new SimpleDateFormat("dd/MM");

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<TurnoAtencion> turnos = service.allTurnosByConfiguracion(config);
        Map<Integer, List<TurnoAtencion>> mapTurnos = new HashMap();

        for (TurnoAtencion turno : turnos) {
            List<TurnoAtencion> turnosHora = mapTurnos.get(turno.getTurno());
            turnosHora = (turnosHora == null) ? new ArrayList() : turnosHora;
            turnosHora.add(turno);
            mapTurnos.put(turno.getTurno(), turnosHora);
        }
        
        ArrayNode lst = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstHoras = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstDias = new ArrayNode(JsonNodeFactory.instance);
        for (Map.Entry<Integer, List<TurnoAtencion>> map : mapTurnos.entrySet()) {
            List<TurnoAtencion> turnosHora = map.getValue();
            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
            ObjectNode objNodeDias = new ObjectNode(JsonNodeFactory.instance);
            objNode.put("hora", turnosHora.get(0).getHoraInicio());
            objNode.put("turnos", new TurnoAtencion().toJsonArray(turnosHora));
            objNodeDias.put("dias", formatter.format(turnosHora.get(0).getFecha()));
            lstHoras.add(objNode);
            lstDias.add(objNodeDias);
        }
        lst.add(lstHoras);
        lst.add(lstDias);
        response.setData(lst);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "configuracion", method = RequestMethod.POST)
    public JsonResponse saveConfiguracion(@RequestBody ConfiguracionTurnosAtencion config, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            service.saveConfiguracion(config);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "updateconfiguracion", method = RequestMethod.POST)
    public JsonResponse updateConfiguracion(@RequestParam(value="name", required=true) String name,@RequestParam(value="value", required=true) String value,@RequestParam(value="pk", required=true) Long pk, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            System.err.println("VAlor" + pk);
            service.updateConfiguracion(pk,value);
            
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
