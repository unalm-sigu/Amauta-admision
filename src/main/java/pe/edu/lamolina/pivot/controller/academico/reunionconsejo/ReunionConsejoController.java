package pe.edu.lamolina.pivot.controller.academico.reunionconsejo;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/reunionconsejo")
public class ReunionConsejoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReunionConsejoService reunionConsejoService;

    Oficina oficinaAux = new Oficina(8L);

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
        model.addAttribute("ciclo", ds.getCicloAcademico());

        return "academico/reunionconsejo/reunionconsejo";
    }

    @ResponseBody
    @RequestMapping("loadModalReunionConsejo")
    public JsonResponse loadModalReunionConsejo(
            @RequestParam("fechaReunion") Date fechaReunion,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            ObjectNode node = new ObjectNode(jsonFactory);

            ReunionConsejo reunionConsejo = reunionConsejoService.findReunionConsejoByFechaAndOficina(fechaReunion, oficinaAux);
            if (reunionConsejo == null) {
                reunionConsejo = new ReunionConsejo();
                reunionConsejo.setEsOrdinario(BigDecimal.ONE.intValue());
            }

            node.set("reunionConsejo", JsonHelper.createJson(reunionConsejo, jsonFactory, true, new String[]{
                "*",
                "usuarioRegistro.*",
                "usuarioActualizacion.*",}));

            response.setData(node);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveReunionConsejo")
    public JsonResponse saveSeccionGrupo(
            @RequestBody ReunionConsejo reunionConsejo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("fecha " + reunionConsejo.getFecha());
            if (reunionConsejo.getId() == null) {
                reunionConsejoService.saveReunionConsejo(reunionConsejo, oficinaAux, ds);
            } else {
                reunionConsejo.setOficina(oficinaAux);
                reunionConsejoService.updateReunionConsejo(reunionConsejo, ds);
            }
            String message = "Reunion consejo grabada correctamente.";
            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allcalendar")
    public JsonResponse allcalendar(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<EventCalendar> eventos = reunionConsejoService.allcalendar(ciclo, oficinaAux);
            for (EventCalendar evento : eventos) {
                jsonList.add(evento.toJson());
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("listReunionesConsejo")
    public DynatableResponse listGrupoHorariosZetas(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<ReunionConsejo> reunionesConsejo = reunionConsejoService.allReunionConsejoByDyna(filter, oficinaAux);

            JsonNodeFactory nf = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(nf);
            for (ReunionConsejo reunionConsejo : reunionesConsejo) {
                array.add(JsonHelper.createJson(reunionConsejo, nf, new String[]{
                    "*",
                    "oficina.*"
                }));
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

}
