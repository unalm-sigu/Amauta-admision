package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

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
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/consejero")
public class ConsejeriaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConsejeroService service;

    @Autowired
    CarreraService carreraService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<Carrera> carreras = service.allCarreraByPersonaCiclo(ds.getPersona(), ds.getCicloAcademico());

        model.addAttribute("ciclo", createCicloJson(ds.getCicloAcademico()).toString());
        model.addAttribute("carreras", createCarrerasJson(carreras).toString());

        return "consejeria/consejero";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<Consejero> consejeros = service.allConsejerosbyDynatableCarrera(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Consejero consjros : consejeros) {
                ObjectNode node = JsonHelper.createJson(consjros, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "estado", "alumnosActivos", "alumnosInactivos",
                            "colaborador.persona.nombreCompleto",
                            "colaborador.persona.numeroDocIdentidad",
                            "colaborador.persona.docente.departamentoAcademico.nombre",
                            "colaborador.persona.docente.codigo",
                            "colaborador.persona.docente.departamentoAcademico.id"
                        });

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());

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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
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

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveConsejero")
    public JsonResponse saveConsejero(@RequestBody Docente docente, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();

        System.out.println("estado" + docente.getEstadoEnum());
        System.out.println("id_persona " + docente.getPersona().getId());
        System.out.println("id_dep " + docente.getDepartamentoAcademico().getId());
        System.out.println("carrera " + docente.getCarrera().getId());

        try {

            if (service.finByIdPersona(docente.getPersona()) != null) {
                json.setMessage("El docente seleccionado ya se encuntra como consejero ");
            } else {
                service.saveConsejeroByDocente(docente, ds);
                json.setMessage("El Docente seleccionado ahora es Consejero.");
                json.setSuccess(true);
            }

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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            
            service.updateEstado(consejero, ds);

            response.setMessage("El estado del consejero fue modificado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
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
