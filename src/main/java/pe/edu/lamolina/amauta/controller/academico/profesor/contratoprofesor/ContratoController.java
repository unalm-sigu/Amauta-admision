package pe.edu.lamolina.amauta.controller.academico.profesor.contratoprofesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;

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
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ContratoDocente cd : contratos) {
                array.add(JsonHelper.createJson(cd, JsonNodeFactory.instance, new String[]{
                    "id",
                    "categoria.*",
                    "situacion.*",
                    "dedicacion.*",
                    "estadoEnum",
                    "resolucionFacultad.id",
                    "resolucionFacultad.serie",
                    "resolucionFacultad.numero",
                    "resolucionFacultad.descripcion",
                    "resolucionFacultad.fechaRegistro",
                    "resolucionConsejo.id",
                    "resolucionConsejo.serie",
                    "resolucionConsejo.numero",
                    "resolucionConsejo.descripcion",
                    "resolucionConsejo.fechaRegistro",
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
                    "id", "descripcion"}));
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
                System.out.println("Descripcion ::" + resolucion.getDescripcion());
                jCiclo.add(JsonHelper.createJson(resolucion, jsonFactory, new String[]{
                    "id", "descripcion"}));
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

    @ResponseBody
    @RequestMapping(value = "/generar/general", method = RequestMethod.POST)
    public String generar(@RequestBody CicloAcademico cicloOrigen, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.generarGeneral(cicloOrigen, ds.getCicloAcademico(), ds);
        return GlobalMessages.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/eliminar/general", method = RequestMethod.GET)
    public String eliminar(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.eliminarGeneral(ds.getCicloAcademico());
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping(value = "/eliminar/contrato/docente/{idContratoDocente}", method = RequestMethod.GET)
    public String eliminarContratoDocente(HttpSession session, @PathVariable("idContratoDocente") Long idContratoDocente) {

        service.eliminarContratoDocente(new ContratoDocente(idContratoDocente));
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping(value = "/all/ciclo/contrato", method = RequestMethod.GET)
    public ArrayNode allCiclo(HttpSession session) {

        return JaneHelper.from(service.allCicloAcademicoContrato())
                .only("id,descripcion,codigo")
                .array();
    }

    @ResponseBody
    @RequestMapping(value = "/all/data/contrato", method = RequestMethod.GET)
    public ObjectNode data(HttpSession session) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.set("ciclos", JaneHelper.from(service.allCicloAcademico()).only("id,codigo,descripcion").array());
        objectNode.set("categorias", JaneHelper.from(service.allCategorias()).array());
        objectNode.set("situaciones", JaneHelper.from(service.allSituaciones()).array());
        objectNode.set("dedicaciones", JaneHelper.from(service.allDedicaciones()).array());
        return objectNode;
    }

    @ResponseBody
    @RequestMapping(value = "/contrato/update/profesor", method = RequestMethod.POST)
    public String updateContratoDocente(@RequestBody ContratoDocente contratoDocente, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.updateContratoDocente(ds, contratoDocente);
        return GlobalMessages.UPDATED;
    }
}
