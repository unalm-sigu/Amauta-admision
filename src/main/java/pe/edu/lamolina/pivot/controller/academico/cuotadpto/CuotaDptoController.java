package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/cuotadpto")
public class CuotaDptoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaDptoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/cuotadpto/cuotaDepartamento";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, @RequestParam(name = "grupoHoras", required = false) Long idGrupoHoras, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<CuotasGrupoHoras> cuotagpohoras = service.allCuotasGpoHoras(filter, new GrupoHoras(idGrupoHoras), ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (CuotasGrupoHoras cuota : cuotagpohoras) {
                ObjectNode node = JsonHelper.createJson(cuota, JsonNodeFactory.instance, true,
                        new String[]{
                            "anexoBoletin.id", "anexoBoletin.nombre", "anexoBoletin.codigo", "anexoBoletin.estado",
                            "grupoHoras.codigo", "grupoHoras.letra", "grupoHoras.tipoCiclo",
                            "cicloAcademico.descripcion2",
                            "cuotas", "asignadasSistema", "totalUtilizadas",
                            "gruposUtilizados",
                            "horasUtilizadas",
                            "detalleGrupos"
                        });

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
    @RequestMapping("allGrupos")
    public JsonResponse allGrupos(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<GrupoHoras> gruposHoras = service.allGrupos();

            ArrayNode arrayGrupos = new ArrayNode(jsonFactory);
            for (GrupoHoras grupo : gruposHoras) {
                ObjectNode json = createGrupoJson(grupo);
                arrayGrupos.add(json);
            }

            response.setData(arrayGrupos);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createGrupoJson(GrupoHoras grupo) {
        ObjectNode json = JsonHelper.createJson(grupo, JsonNodeFactory.instance, true, new String[]{
            "id", "letra"
        });
        return json;
    }

}
