package pe.edu.lamolina.amauta.controller.matricula.configuracionturno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_REG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_REI;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_VER;
import pe.edu.lamolina.model.enums.TipoMatriculaEnum;
import pe.edu.lamolina.amauta.controller.interceptor.InterceptorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/configuracionturno")
public class ConfiguracionTurnoController {

    @Autowired
    ConfiguracionMatriculaService service;

    @Autowired
    InterceptorService interceptorService;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<EventoCicloAcademico> eventosCiclo = service.allEventoCiclo(ds.getCicloAcademico());
        List<ConfiguracionTurnosAtencion> configuraciones = service.allConfiguraciones(ds.getCicloAcademico());
        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);

        for (TipoMatriculaEnum d : TipoMatriculaEnum.values()) {
            objNode.put(d.name(), d.getValue());
        };
        model.addAttribute("eventos", createEventosCicloJson(eventosCiclo));
        model.addAttribute("configuraciones", createCfgTurnosAtencionJson(configuraciones));
        model.addAttribute("ciclo", createCicloJson(ds.getCicloAcademico()));
        model.addAttribute("tipoMatricula", objNode.toString());

        return "academico/matricula/matriculaConfiguracion";
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode node = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "*", "modalidadEstudio.*"
        });
        return node;
    }

    private ArrayNode createCfgTurnosAtencionJson(List<ConfiguracionTurnosAtencion> configuraciones) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode eventosJson = new ArrayNode(JsonNodeFactory.instance);

        for (ConfiguracionTurnosAtencion config : configuraciones) {
            ObjectNode node = JsonHelper.createJson(config, factory, true, new String[]{
                "*", "eventoCicloAcademico.*", "eventoCicloAcademico.eventoAcademico.*"
            });
            eventosJson.add(node);

        }
        return eventosJson;
    }

    private ArrayNode createEventosCicloJson(List<EventoCicloAcademico> eventosCiclo) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode eventosJson = new ArrayNode(JsonNodeFactory.instance);

        for (EventoCicloAcademico evento : eventosCiclo) {
            if (Arrays.asList(MAT_REG.name(), MAT_REI.name(), MAT_VER.name()).contains(evento.getEventoAcademico().getCodigo())) {
                ObjectNode node = JsonHelper.createJson(evento, factory, true, new String[]{
                    "*",
                    "cicloAcademico.id", "cicloAcademico.descripcion",
                    "eventoAcademico.*",
                    "color.*"
                });
                eventosJson.add(node);
            }

        }
        return eventosJson;
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public JsonResponse list(@RequestBody ConfiguracionTurnosAtencion config, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DateFormat formatter = new SimpleDateFormat("dd/MM");

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            List<TurnoAtencion> turnos = service.allTurnosByConfiguracion(config);
            Map<String, List<TurnoAtencion>> mapTurnos = TypesUtil.convertListToMapList("horaInicio", turnos);

            ArrayNode jsonArray = new ArrayNode(JsonNodeFactory.instance);
            ArrayNode horas = new ArrayNode(JsonNodeFactory.instance);
            ArrayNode dias = new ArrayNode(JsonNodeFactory.instance);

            int i = 1;
            for (Map.Entry<String, List<TurnoAtencion>> map : mapTurnos.entrySet()) {
                List<TurnoAtencion> turnosHora = map.getValue();
                ObjectNode nodeHoraTurno = new ObjectNode(JsonNodeFactory.instance);
                if (i == 1) {
                    for (TurnoAtencion lstDia : turnosHora) {
                        ObjectNode objNodeDias = new ObjectNode(JsonNodeFactory.instance);
                        objNodeDias.put("dias", formatter.format(lstDia.getFecha()));
                        dias.add(objNodeDias);
                    }
                    i++;
                }
                nodeHoraTurno.put("hora", turnosHora.get(0).getHoraInicio() + " - " + turnosHora.get(0).getHoraFinal());
                nodeHoraTurno.set("turnos", new TurnoAtencion().toJsonArray(turnosHora));
                horas.add(nodeHoraTurno);

            }

            jsonArray.add(horas);
            jsonArray.add(dias);
            response.setData(jsonArray);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "configuracion", method = RequestMethod.POST)
    public JsonResponse saveConfiguracion(@RequestBody ConfiguracionTurnosAtencion config, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Long Id = service.saveConfiguracion(config);

            response.setSuccess(true);
            response.setData(Id);
            response.setMessage("Se guardó la configuración satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "updateturnos", method = RequestMethod.POST)
    public JsonResponse updateTurnos(@RequestParam(value = "name", required = true) String name, @RequestParam(value = "value", required = true) String value, Model model, @RequestParam(value = "pk", required = true) Long pk, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            ConfiguracionTurnosAtencion config = service.updateTurnos(pk, value);
            JsonResponse json = list(config, model, session);
            response.setData(json);
            response.setSuccess(true);
            response.setMessage("Se actualizó la configuración satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "deleteconfiguracion", method = RequestMethod.DELETE)
    public JsonResponse deleteConfiguracion(@RequestBody ConfiguracionTurnosAtencion config, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            service.deleteConfiguracion(config);
            response.setSuccess(true);
            response.setMessage("Se eliminó la configuración satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
