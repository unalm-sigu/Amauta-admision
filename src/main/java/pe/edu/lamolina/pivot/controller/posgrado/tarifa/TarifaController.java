package pe.edu.lamolina.pivot.controller.posgrado.tarifa;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/tarifa")
public class TarifaController {

    @Autowired
    TarifaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloAcademico().getDescripcion());

        List<Carrera> list = service.allCarreraMaestria();
        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        for (Carrera item : list) {
            arr.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"id", "codigo", "tipoEnum", "nombre"}));
        }
        model.addAttribute("carreras", arr);

        List<CicloAcademico> ciclos = service.allCicloAcademico();
        ArrayNode arrCiclos = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico item : ciclos) {
            arrCiclos.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"id", "descripcion", "descripcion2"}));
        }
        model.addAttribute("ciclos", arrCiclos);

        List<ConceptoPosgrado> conceptos = service.allConceptoPosgrado();
        ArrayNode arrConceptos = new ArrayNode(JsonNodeFactory.instance);
        for (ConceptoPosgrado item : conceptos) {
            arrConceptos.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"*"}));
        }
        model.addAttribute("conceptosPosgrado", arrConceptos);

        return "posgrado/tarifa/tarifa";
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<TarifaCarrera> list = service.allByDynatable(filter);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (TarifaCarrera item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "carrera.id",
                "carrera.nombre",
                "cicloInicio.id",
                "cicloInicio.descripcion",
                "cicloInicio.descripcion2"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public JsonResponse find(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        try {
            TarifaCarrera tarifa = service.find(id);
            response.setData(JsonHelper.createJson(tarifa, JsonNodeFactory.instance, new String[]{
                "*",
                "carrera.id",
                "carrera.nombre",
                "cicloInicio.id",
                "cicloInicio.descripcion",
                "cicloInicio.descripcion2",
                "tarifaConcepto.*",
                "tarifaConcepto.conceptoPosgrado.*"
            }));
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResponse save(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            if (tarifa.getId() != null) {
                service.update(tarifa, ds);
                response.setMessage("Tarifa actualizada");
            } else {
                service.save(tarifa, ds);
                response.setMessage("Tarifa registrada");
            }
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/activar", method = RequestMethod.POST)
    public JsonResponse activar(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.activar(tarifa, ds);
            response.setMessage("Tarifa activada");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/eliminar", method = RequestMethod.POST)
    public JsonResponse eliminar(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();
        try {
            service.eliminar(tarifa, ds);
            response.setMessage("Tarifa eliminada");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
