package pe.edu.lamolina.pivot.controller.permisoprogramahorario;

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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.ColaboradorAnexoBean;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("permisoprograma/buscar")
public class PermisoProgramacionController {

    @Autowired
    PermisoProgramacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        model.addAttribute("cicloacademico", cicloAcademico.getDescripcion());

        return "permisoprograma/permisoprograma";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<ColaboradorAnexoBean> permisosProgramacionHorarios = service.allPermisos(filter);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (ColaboradorAnexoBean colaborador : permisosProgramacionHorarios) {
                ObjectNode node = JsonHelper.createJson(colaborador, JsonNodeFactory.instance, new String[]{
                    "colaborador.*",
                    "colaborador.cargo.*",
                    "colaborador.persona.*",
                    "anexoBoletin.*",
                    "anexoBoletin.*",});
                ArrayNode arrayPermisos = new ArrayNode(JsonNodeFactory.instance);
//                for (PermisosProgramacionHorarios item : colaborador.getPermisosProgramacionHorarioses()) {
//                    ObjectNode permisos = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
//                        "*",
//                        "anexoBoletin.*",
//                        "permisoProgracion.*"
//                    });
//                    arrayPermisos.add(permisos);
//                }
//                node.set("permisosProgramacions", arrayPermisos);
//                arrayNode.add(node);
            }
            json.setData(arrayNode);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(colaborador, ds);

            response.setMessage("Se registró satisfactoriamente");
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
    public JsonResponse update(@RequestBody Colaborador colaborador, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.update(colaborador, ds);

            response.setMessage("Se actualizó satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
