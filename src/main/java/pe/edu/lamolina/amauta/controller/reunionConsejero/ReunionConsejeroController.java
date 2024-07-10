package pe.edu.lamolina.amauta.controller.reunionConsejero;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.reunionConsejero.view.ReunionesConsejerosExcelView;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import static pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum.ANU;
import pe.edu.lamolina.model.enums.ReunionAlumnoConsejeroEstadoEnum;
import pe.edu.lamolina.model.horario.Hora;

@Controller
@RequestMapping("consejeria/agendaconsejero")
public class ReunionConsejeroController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReunionConsejeroService service;

    @Autowired
    ReunionesConsejerosExcelView reunionesConsejerosExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<Consejero> consejeros = service.allConsejeros(ds.getPersona());
        List<Hora> hora = service.allHora30();

        ArrayNode jHora = new ArrayNode(JsonNodeFactory.instance);
        for (Hora rolexamen : hora) {
            jHora.add(JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*"
                    }));
        }

        ArrayNode jConsejeros = new ArrayNode(JsonNodeFactory.instance);
        for (Consejero consejero : consejeros) {
            jConsejeros.add(JsonHelper.createJson(consejero, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "carrera.*",}));
        }

        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance));
        model.addAttribute("jHora", jHora);
        model.addAttribute("jConsejeros", jConsejeros);
        return "consejeria/agendaConsejero/agendaConsejero";
    }

    @ResponseBody
    @RequestMapping("list/{carrera}")
    public DynatableResponse list(@PathVariable Long carrera, DynatableFilter filter, HttpSession session) {

        DynatableResponse response = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Consejero consejero = service.findConsejeroCarrera(carrera, ds.getPersona());
            List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = service.listDynatable(filter, consejero, ds);
            Map<Long, List<ReunionAlumnoConsejero>> map = TypesUtil.convertListToMapList("agendaConsejero.id", reunionAlumnoConsejeros);
            List<AgendaConsejero> agendaConsejeros = reunionAlumnoConsejeros.stream().map(x -> x.getAgendaConsejero()).distinct().collect(Collectors.toList());

            //service.verificarVencimiento(agendaConsejeros);
            //Collections.sort(agendaConsejeros,(AgendaConsejero a, AgendaConsejero b) -> a.getEstadoEnum().getValuePrioridad().compareTo(b.getEstadoEnum().getValuePrioridad()));

            ArrayNode arrayNodeAgenda = new ArrayNode(JsonNodeFactory.instance);
            for (AgendaConsejero agendaConsejero : agendaConsejeros) {
                ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
                ObjectNode objectNode = JsonHelper.createJson(agendaConsejero, JsonNodeFactory.instance, new String[]{"*", "hora.*"});
                for (ReunionAlumnoConsejero reunionAlumnoConsejero : map.get(agendaConsejero.getId())) {

                    arrayNode.add(JsonHelper.createJson(reunionAlumnoConsejero, JsonNodeFactory.instance, new String[]{
                        "*",
                        "alumnoConsejero.*",
                        "alumnoConsejero.alumno.*",
                        "alumnoConsejero.alumno.carrera.*",
                        "alumnoConsejero.alumno.carrera.facultad.*",
                        "alumnoConsejero.alumno.persona.*",
                        "agendaConsejero.*",
                        "agendaConsejero.hora.*"}));

                }
                objectNode.set("reunionAlumnoConsejeros", arrayNode);
                arrayNodeAgenda.add(objectNode);
            }
            response.setData(arrayNodeAgenda);
            response.setFiltered(filter.getFiltered());
            response.setTotal(filter.getTotal());

//            // response.setData();
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("search")
    public JsonResponse search(@RequestParam String searchTerm, DynatableFilter filter, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            List<Consejero> consejeros = service.allConsejeros(ds.getPersona());
            Consejero consejero = service.findConsejeroCarrera(consejeros.get(0).getCarrera().getId(), ds.getPersona());
            List<AlumnoConsejero> alumnoConsejeros = service.listBusca(consejero, ds,searchTerm);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoConsejero alumnoConsejero : alumnoConsejeros) {
                arrayNode.add(JsonHelper.createJson(alumnoConsejero, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.id",
                    "alumno.codigo",
                    "alumno.persona.*",
                    "alumno.persona.apellidosNombres",
                    "alumno.carrera.nombre",
                    "alumno.carrera.facultad.nombre",
                    "alumno.situacionAcademica.nombre"
                }));
            }
            response.setData(arrayNode);
            response.setSuccess(Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("listAlumnos/{carrera}")
    public JsonResponse listAlumnos(@PathVariable Long carrera, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Consejero consejero = service.findConsejeroCarrera(carrera, ds.getPersona());
            List<AlumnoConsejero> alumnoConsejeros = service.list(consejero, ds);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoConsejero alumnoConsejero : alumnoConsejeros) {
                arrayNode.add(JsonHelper.createJson(alumnoConsejero, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.id",
                    "alumno.codigo",
                    "alumno.persona.*",
                    "alumno.persona.apellidosNombres",
                    "alumno.carrera.nombre",
                    "alumno.carrera.facultad.nombre",
                    "alumno.situacionAcademica.nombre"
                }));
            }
            response.setData(arrayNode);
            response.setSuccess(Boolean.TRUE);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findAgenda/{agendaId}")
    public JsonResponse findAgenda(@PathVariable Long agendaId, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            AgendaConsejero agendaConsejero = service.findAgenda(agendaId, ds.getCicloAcademico());

            ObjectNode objectNode = JsonHelper.createJson(agendaConsejero, JsonNodeFactory.instance, new String[]{"*", "hora.*"});
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoConsejero alumnoConsejero : agendaConsejero.getAlumnoConsejeros()) {
                arrayNode.add(JsonHelper.createJson(alumnoConsejero, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.id",
                    "alumno.codigo",
                    "alumno.persona.*",
                    "alumno.persona.apellidosNombres",
                    "alumno.carrera.nombre",
                    "alumno.carrera.facultad.nombre"}));
            }
            objectNode.set("alumnoConsejeros", arrayNode);
            response.setData(objectNode);
            response.setSuccess(Boolean.TRUE);
//            response.setData();
        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody AgendaConsejero agendaConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(agendaConsejero, ds);
            response.setMessage("Se registró la reunión satisfactoriamente.");
            response.setSuccess(true);

//            response.setData();
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody AgendaConsejero agendaConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.update(agendaConsejero, ds);
            response.setMessage("Se actualizó la reunión satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asistenciaReunion")
    public JsonResponse asistenciaReunion(@RequestBody ReunionAlumnoConsejero reunionAlumnoConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.asistenciaReunion(reunionAlumnoConsejero, ds);
            response.setMessage("Se actualizó la reunión satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("inasistenciaReunion")
    public JsonResponse inasistenciaReunion(@RequestBody ReunionAlumnoConsejero reunionAlumnoConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.inasistenciaReunion(reunionAlumnoConsejero, ds);
            response.setMessage("Se actualizó el registro satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularReunion")
    public JsonResponse anularReunion(@RequestBody ReunionAlumnoConsejero reunionAlumnoConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.anularReunion(reunionAlumnoConsejero, ds);
            response.setMessage("Se anuló la reunión satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularAgenda")
    public JsonResponse anularAgenda(@RequestBody AgendaConsejero agendaConsejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.anularAgenda(agendaConsejero, ds);
            response.setMessage("Se anuló la agenda satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("reporteReuniones/{idCarrera}")
    public ModelAndView reporteReuniones(
            @PathVariable("idCarrera") Long idCarrera,
            Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

//        Carrera carrera = new Carrera(idCarrera);
        Consejero consejero = service.findConsejeroCarrera(idCarrera, ds.getPersona());
        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = service.listDynatable(filter, consejero, ds);

        reunionAlumnoConsejeros = reunionAlumnoConsejeros.stream().filter(x -> x.getAgendaConsejero().getEstadoEnum() != ANU && x.getEstadoEnum() != ReunionAlumnoConsejeroEstadoEnum.ANU).collect(Collectors.toList());

        model.addAttribute("reunionAlumnoConsejeros", reunionAlumnoConsejeros);
        model.addAttribute("consejero", consejero);
        return new ModelAndView(reunionesConsejerosExcelView);
    }

}
