package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionGeneradas;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.controller.academico.resolucion.ResolucionService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/resolucion")
public class ResolucionReincorporacionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionReincorporacionService service;

    @Autowired
    ResolucionService resolucionService;

    @Autowired
    MatriculableService matriculableService;

    private MultipartFile resolucionFile;

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

    @RequestMapping(value = "reincorporacion", method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ArrayNode oficinasJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode ciclosJson = new ArrayNode(JsonNodeFactory.instance);
        List<CicloAcademico> cicloAcademicos = resolucionService.allCiclosToReincorporacion();
        List<Oficina> oficinas = resolucionService.allOFicinasByUser(ds);
        for (Oficina oficina : oficinas) {
            ObjectNode oficinaJson = JsonHelper.createJson(oficina, JsonNodeFactory.instance, new String[]{"*"});
            oficinasJson.add(oficinaJson);
        }
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            ObjectNode cicloJson = JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"});
            ciclosJson.add(cicloJson);
        }
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("ciclos", ciclosJson);
        return "academico/resolucion/resolucionreincorporacion/resolucionReincorporacion";
    }

    @ResponseBody
    @RequestMapping("findAlumno")
    public JsonResponse findAlumno(
            @RequestParam("nombre") String nombre,
            @RequestParam("instanciaOficina") Long instanciaOficina,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<Alumno> alumnos = service.allAlumnoDesertorByNombre(nombre, instanciaOficina);
            for (Alumno alumno : alumnos) {
                data.add(JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
                    "id",
                    "codigo",
                    "persona.nombreCompleto",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.*",
                    "carrera.facultad.*",}));
            }
            response.setSuccess(Boolean.TRUE);
            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Resolucion resolucion,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<Alumno> alumnos = service.save(resolucion, ds.getUsuario(), ds);

            response.setMessage("Se realizó el registro satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("alumnos/{idResolucion}")
    public JsonResponse alumnos(@PathVariable(value = "idResolucion") Long resolucion,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<Reincorporacion> reincorporaciones = service.findByResolucion(resolucion, ds);

            for (Reincorporacion reicorporacion : reincorporaciones) {
                array.add(JsonHelper.createJson(reicorporacion, JsonNodeFactory.instance, new String[]{
                    "*",
                    "facultad.*",
                    "alumno.*",
                    "alumno.id",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "cicloReincorporacion.*"
                }));
            }
            response.setSuccess(Boolean.TRUE);
            response.setData(array);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
