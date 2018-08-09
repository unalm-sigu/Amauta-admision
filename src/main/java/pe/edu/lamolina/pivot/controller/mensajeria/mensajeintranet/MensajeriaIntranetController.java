package pe.edu.lamolina.pivot.controller.mensajeria.mensajeintranet;

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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("mensajeria")
public class MensajeriaIntranetController {

    @Autowired
    MensajeriaIntranetService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        List<GrupoAlumno> grupos = service.allGruposAlumnos();
        List<TipoMensajeIntranet> tipos = service.allTiposMensajes();

        ArrayNode arrayGrupos = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arrayTipos = new ArrayNode(JsonNodeFactory.instance);

        for (GrupoAlumno objGrupo : grupos) {
            ObjectNode json = JsonHelper.createJson(objGrupo, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayGrupos.add(json);
        }
        for (TipoMensajeIntranet objTipoMsj : tipos) {
            ObjectNode json = JsonHelper.createJson(objTipoMsj, JsonNodeFactory.instance, new String[]{
                "*"
            });
            arrayTipos.add(json);
        }
        ObjectNode cicloJSON = new ObjectNode(JsonNodeFactory.instance);
        cicloJSON.put("descripcion", ds.getCicloAcademico().getDescripcion());

        model.addAttribute("gruposAlumno", arrayGrupos);
        model.addAttribute("tiposMensaje", arrayTipos);
        model.addAttribute("ciclo", cicloJSON);

        return "mensaje/mensajeriaIntranet/mensajeriaIntranet";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<MensajeIntranet> mensajes = service.allByDynatble(filter);
            for (MensajeIntranet mensaje : mensajes) {
                ObjectNode obj = JsonHelper.createJson(mensaje, JsonNodeFactory.instance, new String[]{
                    "*",
                    "cicloAcademico.id",
                    "cicloAcademico.descripcion",
                    "grupoAlumno.id",
                    "grupoAlumno.nombre",
                    "tipoMensajeIntranet.id",
                    "tipoMensajeIntranet.nombre",
                    "tipoMensajeIntranet.contenido"
                });
                array.add(obj);
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
    @RequestMapping("saveUpdate")
    public JsonResponse saveUpdateMensajeria(@RequestBody MensajeIntranet mensajeria, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            if (mensajeria.getId() == null) {
                service.saveMensajeria(mensajeria, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(Messages.CREATED);
            } else {
                service.updateMensajeria(mensajeria, ds.getCicloAcademico(), ds.getUsuario());
                response.setMessage(Messages.UPDATED);
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody MensajeIntranet mensajeria, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            service.eliminar(mensajeria);
            response.setMessage(Messages.DELETED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
