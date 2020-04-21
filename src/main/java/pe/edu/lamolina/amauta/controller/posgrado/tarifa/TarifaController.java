package pe.edu.lamolina.amauta.controller.posgrado.tarifa;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AmbitoTarifaEnum;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("posgrado/tarifa")
public class TarifaController {

    @Autowired
    TarifaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<Carrera> carreras = service.allCarreraMaestria();
        ArrayNode carrerasJson = new ArrayNode(JsonNodeFactory.instance);
        for (Carrera item : carreras) {
            carrerasJson.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"id", "codigo", "tipoEnum", "nombre"}));
        }

        List<CicloAcademico> ciclos = service.allCicloAcademico();
        ArrayNode ciclosJson = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico item : ciclos) {
            ciclosJson.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"id", "descripcion", "descripcion2"}));
        }

        List<ConceptoPosgrado> conceptos = service.allConceptoPosgrado();
        ArrayNode conceptosJson = new ArrayNode(JsonNodeFactory.instance);
        for (ConceptoPosgrado item : conceptos) {
            conceptosJson.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"*"}));
        }

        model.addAttribute("ciclo", createCicloJson(ds.getCicloAcademico()).toString());
        model.addAttribute("carreras", carrerasJson.toString());
        model.addAttribute("ciclos", ciclosJson.toString());
        model.addAttribute("ambitos", JsonHelper.enumToJson(AmbitoTarifaEnum.values()).toString());
        model.addAttribute("conceptosPosgrado", conceptosJson.toString());

        return "posgrado/tarifa/tarifa";
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<TarifaCarrera> list = service.allByDynatable(filter);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (TarifaCarrera item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "carrera.id",
                "carrera.nombre",
                "carrera.tipoEnum",
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
    @RequestMapping(value = "find/{id}", method = RequestMethod.GET)
    public JsonResponse find(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        try {
            TarifaCarrera tarifa = service.find(id);
            response.setData(JsonHelper.createJson(tarifa, JsonNodeFactory.instance, new String[]{
                "*",
                "carrera.id",
                "carrera.nombre",
                "carrera.tipoEnum",
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
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public JsonResponse save(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
    @RequestMapping(value = "activar", method = RequestMethod.POST)
    public JsonResponse activar(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
    @RequestMapping(value = "eliminar", method = RequestMethod.POST)
    public JsonResponse eliminar(@RequestBody TarifaCarrera tarifa, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

    @ResponseBody
    @RequestMapping("allCiclos")
    public JsonResponse allCiclos(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<CicloAcademico> ciclos = service.allCiclosByNombre(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (CicloAcademico ciclo : ciclos) {
                ObjectNode json = createCicloJson(ciclo);
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode json = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "descripcion", "descripcion2"
        });
        return json;
    }

}
