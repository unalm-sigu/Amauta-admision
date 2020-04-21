package pe.edu.lamolina.pivot.controller.rolexamen.cursosexcluidos;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cursosexcluidos")
public class CursosExcluidosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursosExcluidosService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<RolExamenes> rolexamenes = service.allRolExamenesByCicloActivo(ds.getCicloAcademico());
        ArrayNode jRolesxamenes = new ArrayNode(JsonNodeFactory.instance);
        for (RolExamenes rolexamen : rolexamenes) {
            ObjectNode rolExam = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.cicloAcademico.descripcion",
                        "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
                        "nombre", "estado", "fechaPublicacion"
                    });

            jRolesxamenes.add(rolExam);
        }

        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("jRolesxamenes", jRolesxamenes.toString());
        return "rolexamen/cursosexcluidos/cursosexcluidos";
    }

    @RequestMapping("{rolExamen}")
    public String indexWithRolExamen(
            @PathVariable("rolExamen") Long rolExamenId,
            Model model,
            HttpSession session) {

        RolExamenes rolExamenes = service.findRolExamenes(rolExamenId);
        ObjectNode jRolExamenes = JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false,
                new String[]{
                    "*",
                    "eventoCicloAcademico.eventoAcademico.*",
                    "semanasExamen.rolExamenes.*",
                    "semanasExamen.*",
                    "semanasExamen.horaFin",
                    "semanasExamen.horaInicio"
                });
        model.addAttribute("jRolexamen", jRolExamenes.toString());
        return this.index(model, session);
    }

    @ResponseBody
    @RequestMapping("excluirCurso")
    public JsonResponse excluirCurso(@RequestBody CursoExcluido cursoExcluido, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.excluirCurso(cursoExcluido, ds);
            response.setSuccess(true);
            response.setMessage("Curso excluido satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("excluirSeccion")
    public JsonResponse excluirSeccion(@RequestBody SeccionExcluido seccionExcluido, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.excluirSeccion(seccionExcluido, ds);
            response.setSuccess(true);
            response.setMessage("Sección excluida satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularExcluision")
    public JsonResponse anularExcluision(@RequestBody CursoExcluido cursoExcluido, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.anularExclusion(cursoExcluido, ds);
            response.setSuccess(true);
            response.setMessage("Curso excluido satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("listCursoExcluido")
    public JsonResponse listCursoExcluido(@RequestBody RolExamenes rolExamenes, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            List<CursoExcluido> cursosExcluidos = service.allCursosExcluidosByRolExamenes(rolExamenes);

            ArrayNode jCursoMasivosByRolExamen = new ArrayNode(JsonNodeFactory.instance);
            for (CursoExcluido cursoExcluido : cursosExcluidos) {

                ObjectNode cursoMasivo = JsonHelper.createJson(cursoExcluido, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "rolExamenes.*",
                            "curso.*",
                            "estadoAnulado"
                        });

                jCursoMasivosByRolExamen.add(cursoMasivo);
            }
            response.setData(jCursoMasivosByRolExamen);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("{idRolExamen}/loadSecciones")
    public JsonResponse loadSecciones(
            @RequestParam("nombre") String nombre,
            @PathVariable("idRolExamen") Long idRolExamen,
            HttpSession session,
            HttpServletRequest request) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Seccion> secciones = service.allSeccionesByCicloAndNombreLimit(ds.getCicloAcademico(), new RolExamenes(idRolExamen), nombre);
            ArrayNode jSecciones = new ArrayNode(JsonNodeFactory.instance);
            for (Seccion seccion : secciones) {
                ObjectNode cur = JsonHelper.createJson(seccion, jsonFactory, false,
                        new String[]{
                            "*",
                            "aula.*",
                            "grupoHoras.*",
                            "grupoSeccion.curso.*"
                        });
                jSecciones.add(cur);
            }
            response.setData(jSecciones);

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("loadModalSeccionesExcluidas")
    public JsonResponse loadModalSeccionesExcluidas(@RequestBody CursoExcluido cursoExcluido, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            List<SeccionExcluido> seccionesExcluidas = service.allSeccionesExcluidas(cursoExcluido);

            ArrayNode jSeccionesExcluidas = new ArrayNode(jsonFactory);
            for (SeccionExcluido seccionesExcluida : seccionesExcluidas) {
                ObjectNode jSeccionExcluida = JsonHelper.createJson(seccionesExcluida, jsonFactory, true,
                        new String[]{
                            "*",
                            "seccion.*",
                            "seccion.grupoHoras.*",
                            "seccion.aula.*",
                            "cursoExcluido.*",
                            "cursoExcluido.curso.*"});
                jSeccionesExcluidas.add(jSeccionExcluida);
            }
            response.setSuccess(true);
            response.setData(jSeccionesExcluidas);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activarExclusion")
    public JsonResponse activarExclusion(@RequestBody SeccionExcluido seccionExcluido, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());

            seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
            seccionExcluido = service.updateSeccionExcluidoEstado(seccionExcluido, ds);
            response.setMessage("Exclusión activada correctamente.");
            response.setSuccess(true);
            response.setData(this.jSeccionExcluido(seccionExcluido));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anularExclusion")
    public JsonResponse anularExclusion(@RequestBody SeccionExcluido seccionExcluido, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());

            seccionExcluido.setEstadoEnum(EstadoEnum.ANU);
            seccionExcluido = service.updateSeccionExcluidoEstado(seccionExcluido, ds);
            response.setMessage("Exclusión anulada correctamente.");
            response.setSuccess(true);

            response.setData(this.jSeccionExcluido(seccionExcluido));
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode jSeccionExcluido(SeccionExcluido seccionExcluido) {
        ObjectNode jSeccionExcluido = JsonHelper.createJson(seccionExcluido, JsonNodeFactory.instance, true,
                new String[]{
                    "*",
                    "rolExamenes.*",
                    "cursoExcluido.*",
                    "cursoExcluido.rolExamenes.*",
                    "cursoExcluido.curso.*",
                    "cursoExcluido.estadoAnulado"
                });
        return jSeccionExcluido;
    }

}
