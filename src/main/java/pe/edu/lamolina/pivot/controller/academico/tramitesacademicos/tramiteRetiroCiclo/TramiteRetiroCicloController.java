package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/tramiteretirociclo")
public class TramiteRetiroCicloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteRetiroCicloService service;

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
        List<CicloAcademico> cicloAcademicos = service.allCiclos(ds.getCicloAcademico());
        Parametro parametro = service.findParametro();
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayNode.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{
                "*"}));
        }
        model.addAttribute("ciclos", arrayNode);
        model.addAttribute("rutaMatricula", parametro.getValor());
        model.addAttribute("idUsuario", ds.getUsuario().getId());
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/tramiteRetiroCiclo/tramiteRetiroCiclo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<RetiroCiclo> retiroCiclos = service.allByCiclo(ds.getCicloAcademico(), filter);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (RetiroCiclo cicloAcademico : retiroCiclos) {
                arrayNode.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{
                    "*",
                    "cicloAcademico.*",
                    "alumno.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",}));
            }
            json.setData(arrayNode);
            json.setFiltered(filter.getFiltered());
            json.setTotal(filter.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(
            @RequestBody RetiroCiclo retiroCiclo,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            service.save(retiroCiclo, ds);

            response.setMessage("Se guardó satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(
            @RequestBody RetiroCiclo retiroCiclo,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            MatriculaResumen matriculaResumen =service.update(retiroCiclo, ds);
            
            response.setData(JsonHelper.createJson(matriculaResumen, jsonFactory, new String[]{"id"}));
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
