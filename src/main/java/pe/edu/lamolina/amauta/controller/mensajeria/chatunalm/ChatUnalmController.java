package pe.edu.lamolina.amauta.controller.mensajeria.chatunalm;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.social.UsuarioMensajeria;
import pe.edu.lamolina.model.tramite.CursoDirigido;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("mensajeria/chatunalm")
public class ChatUnalmController {

    private final ChatUnalmService service;

    private final DespliegueConfig despliegueConfig;

    @ResponseBody
    @RequestMapping(value = "allData", method = RequestMethod.POST)
    public JsonResponse allData(@RequestBody CursoDirigido cursoDirigido, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<AsuntoMensaje> asuntos = service.allAsuntos(ds);

        Docente docente = ds.getDocente();
        if (docente == null) {
            docente = new Docente();
        }

        ObjectNode docenteJson = JaneHelper
                .from(docente)
                .only("id,codigo,estado")
                .join("modalidadEstudio", "codigo,nombre")
                .join("departamentoAcademico", "codigo,nombre")
                .join("departamentoAcademico.facultad", "codigo,nombre")
                .json();

        ObjectNode personaJson = JaneHelper
                .from(ds.getPersona())
                .only("nombreCompleto,avatar")
                .join("tipoDocumento", "simbolo")
                .json();

        UsuarioMensajeria tmp = new UsuarioMensajeria();
        tmp.setPersona(ds.getPersona());
        personaJson.put("codigo", tmp.generarUserWS());

        ArrayNode listaAsuntosJson = new ArrayNode(JsonNodeFactory.instance);
        asuntos.forEach(asunto -> {
            ObjectNode asuntoJson = JaneHelper
                    .from(asunto)
                    .only("id,asunto")
                    .join("resumenUsuario", "totalMensajes,pendientesLeer")
                    .join("mensajePrincipal", "id,mensaje,estado,fecha,hora,esHoy")
                    .join("mensajePrincipal.remitente.persona", "nomPaterno")
                    .json();

            ArrayNode mensajesJson = JaneHelper
                    .from(asunto.getMensajes())
                    .only("id,mensaje,estado,fecha,hora,esHoy")
                    .join("remitente.persona", "nomPaterno")
                    .array();

            asuntoJson.set("mensajes", mensajesJson);
            listaAsuntosJson.add(asuntoJson);
        });

        ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
        data.set("asuntos", listaAsuntosJson);
        data.set("docente", docenteJson);
        data.set("persona", personaJson);
        data.put("esTest", !despliegueConfig.isProduccion());

        JsonResponse response = new JsonResponse();
        response.setData(data);
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "marcarMensaje", method = RequestMethod.POST)
    public JsonResponse marcarMensaje(@RequestBody MensajeSistema mensaje, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.marcarMensaje(mensaje, ds);

        JsonResponse response = new JsonResponse();
        response.setMessage("Mensaje leido");
        response.setSuccess(Boolean.TRUE);

        return response;
    }

}
