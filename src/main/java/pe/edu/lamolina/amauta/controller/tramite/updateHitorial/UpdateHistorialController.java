package pe.edu.lamolina.amauta.controller.tramite.updateHitorial;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
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
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import static pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum.CARTA;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/updateHistorial")
public class UpdateHistorialController {

    @Autowired
    UpdateHistorialService service;

    @Autowired
    TramitesAcademicosService tramitesAcademicosService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (TipoDocumentoCompaniaEnum value : TipoDocumentoCompaniaEnum.values()) {
            if (Arrays.asList(CARTA.name(), TipoDocumentoCompaniaEnum.MANUAL.name()).contains(value.name())) {
                ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
                obj.put("name", value.name());
                obj.put("value", value.getValue());
                arrayNode.add(obj);
            }
        }

        ObjectNode obj = JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{
            "id",
            "descripcion"
        });
        model.addAttribute("cicloacademico", obj);
        model.addAttribute("tipoDocumento", arrayNode);
        return "tramite/correccionHisto/correccionHistorial";
    }

    @ResponseBody
    @RequestMapping("updateEstado")
    public JsonResponse cambiarAulaDirect(
            @RequestParam("tramite") Long tramiteId,
            @RequestParam("accionTramite") Long accionTramiteId,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            tramitesAcademicosService.aceptarSolReincorporacion(new Tramite(tramiteId), new AccionTramiteAcademico(accionTramiteId), ds);
            response.setMessage("Solicitud Actualizada.");

            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse response = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

            List<TramiteCorreccionHistorial> tramiteSubv = service.allByCiclo(ds.getCicloAcademico(), filter);
            for (TramiteCorreccionHistorial tramiteCorrhisto : tramiteSubv) {
                ObjectNode obj = JsonHelper.createJson(tramiteCorrhisto, JsonNodeFactory.instance, new String[]{
                    "*",
                    "estadoTramite.*",
                    "archivo.*",
                    "userRegistro.*",
                    "userRegistro.persona.*",
                    "userModificacion.*",
                    "userModificacion.persona.*",
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
                    "tramite.alumno.carrera.facultad.simbolo",
                    "tramite.accionesTramitesAcademico.*",
                    "tramite.accionesTramitesAcademico.estadoTramiteFinal.*",
                    "tramite.accionesTramitesAcademico.estadoTramiteInicio.*",
                    "tramite.formularioEstadoTramite.*"});
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
    public JsonResponse save(@RequestBody TramiteCorreccionHistorial correccionHistorial,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            service.save(correccionHistorial, ds);

            json.setSuccess(true);
            json.setMessage("Se guardó el trámite");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anular(@RequestBody TramiteCorreccionHistorial correccionHistorial,
            Model model, HttpSession session) {
        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            service.anular(correccionHistorial, ds);

            json.setSuccess(true);
            json.setMessage("Se anuló el trámite");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }
}
