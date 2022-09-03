package pe.edu.lamolina.amauta.controller.tramite.bolsatrabajo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/bolsatrabajo")
public class BolsaTrabajoController {

    @Autowired
    BolsaTrabajoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ObjectNode cicloJson = JaneHelper.from(ds.getCicloAcademico()).only("id,descripcion").json();

        model.addAttribute("ciclo", cicloJson);
        return "tramite/bolsatrabajo/bolsaTrabajo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        List<TramiteSubvencion> subvencionesAll = service.allSubvencionesBySupervisor(ds.getPersona(), ds.getCicloAcademico());
        for (TramiteSubvencion subvencion : subvencionesAll) {
            ObjectNode obj = JaneHelper
                    .from(subvencion)
                    .join("tipoSubvencion", "nombre,codigo")
                    .join("tramite", "id,numero,serie,estado,estadoEnum,observacion")
                    .join("tramite.tipoTramite", "nombre,codigo")
                    .join("tramite.alumno", "codigo")
                    .join("tramite.alumno.persona", "apellidosNombres,numeroDocIdentidad,rutaFoto,tipoFoto")
                    .join("tramite.alumno.persona.tipoDocumento", "simbolo")
                    .join("tramite.alumno.carrera", "nombre")
                    .join("tramite.alumno.carrera.facultad", "nombre")
                    .join("tramite.accionTramiteBienestar", "estadoInicio,estadoFinal,queHacer")
                    .join("supervisor", "id")
                    .join("supervisor.persona", "apellidosNombres,emailCompania,numeroDocIdentidad")
                    .join("supervisor.persona.tipoDocumento", "simbolo")
                    .join("supervisor.cargo", "nombre")
                    .join("supervisor.oficina", "codigo,nombre")
                    .join("supervisor.oficina.tipoOficina", "nivel")
                    .join("supervisor.oficina.oficinaSuperior", "codigo,nombre")
                    .json();

            arrayNode.add(obj);
        }

        DynatableResponse response = new DynatableResponse();
        response.setData(arrayNode);
        response.setTotal(arrayNode.size());
        response.setFiltered(arrayNode.size());

        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TramiteSubvencion tramiteSubvencion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.updateTramiteSubvencion(tramiteSubvencion, ds);
            response.setSuccess(true);
            response.setMessage("Se actualizó el trámite");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
