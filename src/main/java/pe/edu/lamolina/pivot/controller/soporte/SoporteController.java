package pe.edu.lamolina.pivot.controller.soporte;

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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/soporte")
public class SoporteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    @Autowired
    SoporteService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam(value = "visualizar", required = false) String stringCiclo,
            Model model, HttpSession session) {

        return "soporte/soporte";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            List<Soporte> soportes = service.list(filter);
            for (Soporte soporte : soportes) {
                ObjectNode objectNodeAlum = JsonHelper.createJson(soporte.getAlumno(), JsonNodeFactory.instance, new String[]{"id", "codigo", "estado", "estadoEnum",
                    "promedioAcumulado", "creditosCursados", "creditosAprobados",
                    "persona.id",
                    "persona.apellidosNombres",
                    "persona.rutaFoto",
                    "persona.tipoFoto",
                    "persona.tipoDocumento.simbolo",
                    "persona.numeroDocIdentidad",
                    "persona.telefono",
                    "persona.celular",
                    "persona.email",
                    "persona.emailCompania",
                    "carrera.nombre",
                    "carrera.codigo",
                    "carrera.tipoEnum",
                    "carrera.tipo",
                    "carrera.facultad.codigo",
                    "carrera.facultad.nombre",
                    "modalidadEstudio.codigo",
                    "situacionAcademica.codigo",
                    "situacionAcademica.nombre",
                    "modalidadEstudio.nombre",
                    "cicloIngreso.descripcion",
                    "cicloActivo.descripcion"});
                ObjectNode objectNode = JsonHelper.createJson(soporte, JsonNodeFactory.instance, new String[]{"*"});
                objectNode.set("alumno", objectNodeAlum);
                arrayNode.add(objectNode);
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
    @RequestMapping("responder")
    public JsonResponse save(@RequestBody Soporte soporte, HttpSession session) {
        JsonResponse json = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.responder(soporte, ds);

            json.setSuccess(true);
            json.setMessage("Modificación satisfactoria.");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }

    }

}
