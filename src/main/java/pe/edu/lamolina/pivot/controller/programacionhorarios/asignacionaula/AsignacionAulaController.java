package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/asignacionaula")
public class AsignacionAulaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AsignacionAulaService asignacionAulaService;

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
        AsignacionAula asignacionAula = asignacionAulaService.findAsignacionAulaByCiclo(ds.getCicloAcademico());

        if (asignacionAula != null) {
            ObjectNode jAsignacionAula = JsonHelper.createJson(asignacionAula,
                    JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "cicloAcademico.*"
                    });
            model.addAttribute("jAsignacionAula", jAsignacionAula.toString());
        }
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("cicloJson", createCicloJson(ds.getCicloAcademico()).toString());

        return "programacion/asignacionaula/asignacionaula";
    }

    @ResponseBody
    @RequestMapping(value = "loadAsignacionAula", method = RequestMethod.POST)
    public JsonResponse loadAsignacionAula(@RequestBody AsignacionAula asignacionAula) {
        JsonResponse response = new JsonResponse();
        try {
            asignacionAula = asignacionAulaService.findAsignacionAula(asignacionAula);
            ObjectNode jAsignacionAula = JsonHelper.createJson(asignacionAula, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "cicloAcademico.*"
                    }
            );
            response.setData(jAsignacionAula);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "procesarAsignacionAulas", method = RequestMethod.POST)
    public JsonResponse procesarAsignacionAulas(@RequestBody AsignacionAula asignacionAula,
            HttpSession session, HttpServletRequest request) {
        logger.debug("procesarAsignacionAulas");
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            asignacionAula = asignacionAulaService.procesarAsignacionAulas(asignacionAula, ds);
            ObjectNode jAsignacionAula = JsonHelper.createJson(asignacionAula,
                    JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "cicloAcademico.*"
                    });
            response.setData(jAsignacionAula);
            response.setMessage("Aulas asignadas correctamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "aliminarAsignacion", method = RequestMethod.POST)
    public JsonResponse aliminarAsignacion(@RequestBody AsignacionAula asignacionAula,
            HttpSession session, HttpServletRequest request) {
        logger.debug("aliminarAsignacion");
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            asignacionAulaService.deleteAsignacion(asignacionAula);
            //     response.setData(jAsignacionAula);
            response.setMessage("Asignación de aulas eliminada.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode nodeJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "descripcion", "descripcion2", "tipo",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre"
        });
        return nodeJson;
    }

}
