package pe.edu.lamolina.amauta.controller.oficinas.matricula.restriccionmatricula;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("oficinas/matricula/restriccionmatricula")
public class RestriccionMatriculaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestriccionMatriculaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Oficina> oficinas = ds.getOficinas();
        if (oficinas.stream().filter(x -> x.isOficinaOera()).findAny().orElse(null) != null) {
            oficinas = service.allOficina();
        }
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = JsonHelper.createJson(oficina, JsonNodeFactory.instance, new String[]{
                "*"});
            array.add(node);
        }

        model.addAttribute("oficinas", array);
        return "oficinas/matricula/restriccionmatricula/restriccionMatricula";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            JsonNodeFactory factory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(factory);
            List<DeudaMaterialAlumno> deudas = service.allDeudaAlumno(filter, ds);

            for (DeudaMaterialAlumno deuda : deudas) {
                ObjectNode node = JsonHelper.createJson(deuda, factory, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.modalidadEstudio.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "oficina.*",});

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

    @ResponseBody
    @RequestMapping("levantar")
    public JsonResponse levantar(@RequestParam("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            DeudaMaterialAlumno deuda = new DeudaMaterialAlumno(id);
            service.levantarDeuda(deuda, ds);
            response.setSuccess(true);
            response.setMessage("Restricción levantada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse deuda(@RequestBody DeudaMaterialAlumno deuda, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            service.anularDeuda(deuda, ds);
            response.setSuccess(true);
            response.setMessage("Restricción anulada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("guardar")
    public JsonResponse guardar(@RequestBody DeudaMaterialAlumno deuda) {
        JsonResponse response = new JsonResponse();
        try {
            service.guardarDeuda(deuda);
            response.setSuccess(true);
            response.setMessage("Restricción actualizada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody DeudaMaterialAlumno deuda, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(deuda, ds);
            response.setSuccess(true);
            response.setMessage("Restricción actualizada");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("upload")
    public String upload(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Oficina> Oficinas = ds.getOficinas();

        model.addAttribute("oficinas", Oficinas);

        return "oficinas/matricula/restriccionmatricula/restriccionMatriculaUpload";

    }

    @ResponseBody
    @RequestMapping("cargarDatos")
    public JsonResponse cargarDatos(@RequestParam("file") MultipartFile file,
            @RequestParam("idOficina") Long oficinaId,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<String> observados = service.cargarDeudas(file, new Oficina(oficinaId), ds);
            if (observados.isEmpty()) {
                json.setData(null);
            } else {
                logger.debug("Hay observaciones");
                JsonNodeFactory factory = JsonNodeFactory.instance;
                ArrayNode observaciones = new ArrayNode(factory);
                for (String observado : observados) {
                    observaciones.add(observado);
                }
                json.setData(observaciones);
            }
            json.setSuccess(true);
            json.setMessage("Restricciones de matrícula guardadas");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }
}
