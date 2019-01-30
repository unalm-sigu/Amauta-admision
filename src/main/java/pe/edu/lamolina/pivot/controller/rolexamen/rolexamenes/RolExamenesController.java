package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/rolexamenes")
public class RolExamenesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        JsonNodeFactory jc = JsonNodeFactory.instance;

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<Hora> horas = service.allHoras();
        ArrayNode jHoras = new ArrayNode(jc);
        horas.forEach(x -> {
            jHoras.add(JsonHelper.createJson(x, jc, false,
                    new String[]{
                        "*"
                    }));
        });
        model.addAttribute("jHoras", jHoras.toString());

        List<EventoCicloAcademico> eventoCicloAcademicos = service.allEventoCicloAcademicos(ds.getCicloAcademico());
        ArrayNode arrayEventosCiclosAcademicos = new ArrayNode(jc);
        for (EventoCicloAcademico eventoCicloAcademico : eventoCicloAcademicos) {
            ObjectNode json = createEventoCicloAcademicoJson(eventoCicloAcademico);
            arrayEventosCiclosAcademicos.add(json);
        }
        model.addAttribute("jEventosCiclosAcademicos", arrayEventosCiclosAcademicos.toString());
        return "rolexamen/rolexamenes/rolexamenes";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<RolExamenes> rolexamenes = service.allRolExamenes(filter, ds.getCicloAcademico());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (RolExamenes rolexamen : rolexamenes) {
                ObjectNode node = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "eventoCicloAcademico.cicloAcademico.descripcion",
                            "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
                            "nombre", "estado", "fechaPublicacion",
                            "userRegistro.persona.apellidosNombres"
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
    @RequestMapping("allEventoCicloAcademico")
    public JsonResponse allEventoCicloAcademico(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<EventoCicloAcademico> eventoCicloAcademicos = service.allEventoCicloAcademicos(ds.getCicloAcademico());
            logger.debug("Estoy fuera del for allEventoCicloAcademico");
            ArrayNode arrayEventosCiclosAcademicos = new ArrayNode(jsonFactory);
            for (EventoCicloAcademico eventoCicloAcademico : eventoCicloAcademicos) {
                logger.debug("Estoy dentro del for allEventoCicloAcademico", eventoCicloAcademico.getEstado());
                ObjectNode json = createEventoCicloAcademicoJson(eventoCicloAcademico);
                arrayEventosCiclosAcademicos.add(json);
            }

            response.setData(arrayEventosCiclosAcademicos);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("loadRolExamenesInfo")
    public JsonResponse loadRolExamenesInfo(@RequestBody RolExamenes rolExamenes, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            rolExamenes = service.findRolExamenes(rolExamenes.getId());
            response.setData(JsonHelper.createJson(rolExamenes, jsonFactory, false,
                    new String[]{
                        "*",
                        "userRegistro.persona.*",
                        "eventoCicloAcademico.*",
                        "eventoCicloAcademico.eventoAcademico.*",
                        "semanasExamen.*",
                        "semanasExamen.horaInicio.*",
                        "semanasExamen.horaFin.*"
                    }));
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("changeEventoCicloAcademico")
    public JsonResponse changeEventoCicloAcademico(@RequestBody EventoCicloAcademico eventoCicloAcademico, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<SemanaExamen> semanasExamen = service.allSemanaExamenByEventoCiclo(eventoCicloAcademico);
            ArrayNode jSemanasExamen = new ArrayNode(jsonFactory);
            semanasExamen.forEach(x -> {
                jSemanasExamen.add(JsonHelper.createJson(x, jsonFactory, true,
                        new String[]{
                            "*",
                            "horaInicio.*",
                            "horaFin.*"
                        }));
            });

            response.setData(jSemanasExamen);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createEventoCicloAcademicoJson(EventoCicloAcademico eventoCicloAcademico) {
        ObjectNode json = JsonHelper.createJson(eventoCicloAcademico, JsonNodeFactory.instance, true, new String[]{
            "eventoAcademico.id", "eventoAcademico.codigo", "eventoAcademico.tipo", "eventoAcademico.nombre",
            "cicloAcademico.id", "cicloAcademico.codigo", "cicloAcademico.descripcion", "cicloAcademico.descripcion2",
            "color.id", "color.codigo",
            "id", "estado", "fechaInicio", "fechaFin", "fechaRegistro"
        });
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody RolExamenes rolExamenes, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (rolExamenes.getId() == null) {
                service.save(rolExamenes, ds);
                response.setMessage("Guardado satisfactoriamnente");
            } else {
                service.update(rolExamenes, ds);
                response.setMessage("Actualizado satisfactoriamnente");
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("publicarrolexamen")
    public JsonResponse publicarrolexamen(@RequestBody RolExamenes rolExamenes, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.publicarRolExamen(rolExamenes, ds);
            response.setSuccess(true);
            response.setMessage("Rol examen publicado.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminarconfiguracion")
    public JsonResponse eliminarconfiguracion(@RequestBody RolExamenes rolExamenes, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.eliminarConfiguracion(rolExamenes, ds);
            response.setSuccess(true);
            response.setMessage("Rol examen publicado.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("fijarhorarioaula")
    public JsonResponse fijarhorarioaula(RolExamenes rolExamenes, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.fijarHorarioAula(rolExamenes, ds);
            response.setSuccess(true);
            response.setMessage("Rol examen publicado.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
