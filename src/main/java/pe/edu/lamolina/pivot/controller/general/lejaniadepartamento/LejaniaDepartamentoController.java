package pe.edu.lamolina.pivot.controller.general.lejaniadepartamento;

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
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.enums.TipoAmbienteEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/lejaniadepartamento")
public class LejaniaDepartamentoController {

    @Autowired
    LejaniaDepartamentoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        return "general/lejaniadepartamento/lejaniadepartamento";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<DistanciaPabellon> distanciaPabellon = service.allDistanciaPabellon(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (DistanciaPabellon distanciaPab : distanciaPabellon) {
                ObjectNode node = JsonHelper.createJson(distanciaPab, JsonNodeFactory.instance, true,
                        new String[]{
                            "departamentoAcademico.id", "departamentoAcademico.codigo", "departamentoAcademico.nombre",
                            "pabellon.id", "pabellon.codigo", "pabellon.nombre", 
                            "id","distancia"
                        });

                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("allDepartamentos")
    public JsonResponse allDepartamentos(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<DepartamentoAcademico> departamentoAcademico = service.allDepartamentos();

            ArrayNode arrayDepartamentos = new ArrayNode(jsonFactory);
            for (DepartamentoAcademico dptoacademico : departamentoAcademico) {
                ObjectNode json = createDepartamentoJson(dptoacademico);
                arrayDepartamentos.add(json);
            }

            response.setData(arrayDepartamentos);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createDepartamentoJson(DepartamentoAcademico dptoacademico) {
        ObjectNode json = JsonHelper.createJson(dptoacademico, JsonNodeFactory.instance, true, new String[]{
            "id", "nombre", "codigo"
        });
        return json;
    }

    @ResponseBody
    @RequestMapping("{idDepartamento}/allFactorDistanciaByDepartamento")
    public JsonResponse allFactorDistanciaByDepartamento(@PathVariable("idDepartamento") Long idDepartamento, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<DistanciaPabellon> distanciaPabellon = service.allFactorDistanciaByDepartamento(new DepartamentoAcademico(idDepartamento));

            ArrayNode arrayFactorDistanciaByDepartamento = new ArrayNode(jsonFactory);
            for (DistanciaPabellon distanciaPab : distanciaPabellon) {
                ObjectNode json = createFactorDistanciaByDepartamentoJson(distanciaPab);
                arrayFactorDistanciaByDepartamento.add(json);
            }

            response.setData(arrayFactorDistanciaByDepartamento);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createFactorDistanciaByDepartamentoJson(DistanciaPabellon distanciaPab) {
        ObjectNode json = JsonHelper.createJson(distanciaPab, JsonNodeFactory.instance, true, new String[]{
            "departamentoAcademico.id", "departamentoAcademico.codigo", "departamentoAcademico.nombre",
            "pabellon.id", "pabellon.codigo", "pabellon.nombre",
            "id", "distancia"
        });
        return json;

    }
    
    @ResponseBody
    @RequestMapping("allModulos")
    public JsonResponse allModulos(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            Oficina oficinaOERA = service.findOficinaOera();
            List<Aula> pabellones = service.allPabellonesByOficina(oficinaOERA);

            ArrayNode arrayPabellones = new ArrayNode(jsonFactory);
            for (Aula pabellon : pabellones) {
                ObjectNode json = createPabellonesJson(pabellon);
                arrayPabellones.add(json);
            }

            response.setData(arrayPabellones);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createPabellonesJson(Aula pabellon) {
        ObjectNode json = JsonHelper.createJson(pabellon, JsonNodeFactory.instance, true, new String[]{
             "id",
             "codigo",
             "nombre"
        });
        return json;
    }
    
    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody List<DistanciaPabellon> distanciaPabellon, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
               service.save(distanciaPabellon, ds);

            response.setSuccess(true);
            response.setMessage("Guardado satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
