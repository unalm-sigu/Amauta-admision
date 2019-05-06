package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/consejeros")
public class ConsejerosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConsejerosService service;

    @Autowired
    CarreraService carreraService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("ciclo academico {}", ds.getCicloAcademico());
        logger.debug("persona id {}", ds.getPersona().getId());

        List<Carrera> carreras = service.allCarreraByPersonaCiclo(ds.getPersona(), ds.getCicloAcademico());
        logger.debug("carrera cantiad {}", carreras.size());

        model.addAttribute("ciclo", createCicloJson(ds.getCicloAcademico()).toString());
        model.addAttribute("carreras", createCarrerasJson(carreras).toString());

        return "consejeria/consejeros/consejeros";
    }

    @ResponseBody
    @RequestMapping("list/{carrera}")
    public DynatableResponse list(
            @PathVariable("carrera") Long idCarrera,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.revisarConsejeria(new Carrera(idCarrera), ds.getCicloAcademico(), false, ds);
            List<Consejero> consejeros = service.allByCarreraDynatable(new Carrera(idCarrera), filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (Consejero consejero : consejeros) {
                ObjectNode node = JsonHelper.createJson(consejero, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "estado", "alumnosActivos", "alumnosInactivos",
                            "colaborador.persona.emailCompania",
                            "colaborador.persona.nombreCompleto",
                            "colaborador.persona.numeroDocIdentidad",
                            "colaborador.persona.tipoDocumento.simbolo",
                            "docente.departamentoAcademico.nombre",
                            "docente.codigo",
                            "docente.departamentoAcademico.id"
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
    @RequestMapping("listDocente")
    public JsonResponse listDocente(
            @RequestParam String nombre,
            @RequestParam Long idFacultad, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {

            List<Docente> docentes = service.allDocenteByNombreFacultad(nombre, new Facultad(idFacultad));

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Docente docente : docentes) {

                ObjectNode node = JsonHelper.createJson(docente, JsonNodeFactory.instance, true, new String[]{
                    "id", "estado", "codigo",
                    "persona.id",
                    "persona.nombreCompleto",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.simbolo",
                    "departamentoAcademico.id",
                    "departamentoAcademico.nombre",
                    "departamentoAcademico.facultad.id"
                });
                array.add(node);
            }
            json.setData(array);
            json.setTotal(array.size());
            json.setMessage("Búsqueda Exitosa");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveConsejero")
    public JsonResponse saveConsejero(@RequestBody Docente docente, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();

        logger.debug("id_persona " + docente.getPersona().getId());
        logger.debug("id_dep " + docente.getDepartamentoAcademico().getId());
        logger.debug("carrera " + docente.getCarrera().getId());

        try {

            service.saveConsejeroByDocente(docente, ds.getCicloAcademico(), ds);
            json.setMessage("El Docente seleccionado ahora es Consejero.");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("cambiarEstado")
    public JsonResponse cambiarEstado(@RequestBody Consejero consejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.updateEstado(consejero, ds.getCicloAcademico(), ds);

            response.setMessage("El estado del consejero fue modificado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("resumenCarrera")
    public JsonResponse resumenCarrera(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ConsejeriaResumen resumen = service.getResumenByCarreraCiclo(new Carrera(idCarrera), ds.getCicloAcademico());
            ObjectNode consejeroJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});
            json.setData(consejeroJson);
            json.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("asignarAlumno")
    public JsonResponse asignarAlumno(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.asignarAlumnosAleatorio(new Carrera(idCarrera), ds.getCicloAcademico(), ds);
            json.setMessage("Los alumnos se asignaron de manera aleatoria satisfactoriamente");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("desasignarAlumno")
    public JsonResponse desasignarAlumno(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.desasignarAlumnos(new Carrera(idCarrera), ds.getCicloAcademico(), ds);
            json.setMessage("Se retiraron los tutores a todos los alumnos satisfactoriamente");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{"id", "descripcion", "descripcion2"});
    }

    private ArrayNode createCarrerasJson(List<Carrera> carreras) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Carrera carrera : carreras) {
            ObjectNode node = JsonHelper.createJson(carrera, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre"
            });
            array.add(node);
        }
        return array;
    }

}
