package pe.edu.lamolina.pivot.controller.academico.profesor.contratoprofesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/profesor")
public class ContratoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContratoService service;

    @ResponseBody
    @RequestMapping("/{id}/contratos")
    public DynatableResponse list(DynatableFilter filter, @PathVariable Long id) {

        DynatableResponse json = new DynatableResponse();

        try {

            List<ContratoDocente> contratos = service.allByDynatable(filter, new Docente(id));
//            List<ContratoDocente> contratos = new ArrayList<>();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ContratoDocente cd : contratos) {
                array.add(JsonHelper.createJson(cd, JsonNodeFactory.instance, new String[]{
                    "id",
                    "categoria.*",
                    "situacion.*",
                    "dedicacion.*",
                    "estadoEnum",
                    "resolucionFacultad.*",
                    "resolucionConsejo.*",
                    "cicloInicioContrato.id",
                    "cicloInicioContrato.descripcion",
                    "cicloFinContrato.id",
                    "cicloFinContrato.descripcion"
                }));
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
    @RequestMapping(value = "/{id}/contratos/save", method = RequestMethod.POST)
    public JsonResponse save(@PathVariable Long id, ContratoDocente contratoDocente, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.save(new Docente(id), contratoDocente, ds);
            response.setMessage("Contrato agregado");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/searchciclo")
    public JsonResponse searchciclo(@RequestParam("nombre") String nombre) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<CicloAcademico> ciclos = service.allCicloByName(nombre);
            ArrayNode jCiclo = new ArrayNode(jsonFactory);
            for (CicloAcademico ciclo : ciclos) {
                jCiclo.add(JsonHelper.createJson(ciclo, jsonFactory, new String[]{
                    "id",
                    "descripcion",
                    "descripcion2"
                }));
            }
            response.setData(jCiclo);
            response.setTotal(jCiclo.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/searchresolucionconsejo")
    public JsonResponse searchresolucionconsejo(@RequestParam("nombre") String nombre) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Resolucion> resoluciones = service.searchResolucionConsejo(nombre);
            ArrayNode jCiclo = new ArrayNode(jsonFactory);
            for (Resolucion resolucion : resoluciones) {
                jCiclo.add(JsonHelper.createJson(resolucion, jsonFactory, new String[]{
                    "id",
                    "descripcion",}));
            }
            response.setData(jCiclo);
            response.setTotal(jCiclo.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/searchresolucionfacultad")
    public JsonResponse searchresolucionfacultad(@RequestParam("nombre") String nombre) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Resolucion> resoluciones = service.searchResolucionFacultad(nombre);
            ArrayNode jCiclo = new ArrayNode(jsonFactory);
            for (Resolucion resolucion : resoluciones) {
                jCiclo.add(JsonHelper.createJson(resolucion, jsonFactory, new String[]{
                    "id",
                    "descripcion",}));
            }
            response.setData(jCiclo);
            response.setTotal(jCiclo.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/{id}/resolucionfacultad")
    public JsonResponse resolucionfacultad(@PathVariable Long id, Resolucion resolucionFacultad, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.addResolucionFacultad(new ContratoDocente(id), resolucionFacultad, ds);
            response.setMessage("Resolución de facultad agregada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/{id}/resolucionconsejo")
    public JsonResponse resolucionconsejo(@PathVariable Long id, Resolucion resolucionConsejo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.addResolucionConsejo(new ContratoDocente(id), resolucionConsejo, ds);
            response.setMessage("Resolución de consejo agregada");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/{id}/vistobueno")
    public JsonResponse vistobueno(@PathVariable Long id, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.addVistoBueno(new ContratoDocente(id), ds);
            response.setMessage("Visto bueno agregado");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/contrato/{id}/finalizar")
    public JsonResponse finalizar(@PathVariable Long id, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            service.finalizar(new ContratoDocente(id), ds);
            response.setMessage("Visto bueno agregado");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
