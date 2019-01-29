package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
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
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/grupoespecial")
public class GrupoEspecialController {

    @Autowired
    GrupoEspecialService grupoEspecialService;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    private enum TipoAccion {
        LETRA,
        SECCION,
        GRUPO,
        ALUMNO
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = grupoEspecialService.allRolExamenesActives(ds.getCicloAcademico());
        JsonNodeFactory jc = JsonNodeFactory.instance;

        ArrayNode jRolesExamenes = new ArrayNode(jc);
        rolesExamenes.forEach(x -> {
            jRolesExamenes.add(JsonHelper.createJson(x, jc, false,
                    new String[]{
                        "*",
                        "eventoCicloAcademico.eventoAcademico.*"
                    }));
        });
        model.addAttribute("jRolesExamenes", jRolesExamenes.toString());

        return "rolexamen/grupoespecial/grupoEspecial";
    }

    @RequestMapping("{rolExamen}")
    public String indexWithRolExamen(
            @PathVariable("rolExamen") Long rolExamenId,
            Model model,
            HttpSession session) {

        RolExamenes rolExamenes = grupoEspecialService.findRolExamenes(rolExamenId);
        ObjectNode jRolExamenes = JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false,
                new String[]{
                    "*",
                    "eventoCicloAcademico.eventoAcademico.*",
                    "semanasExamen.rolExamenes.*",
                    "semanasExamen.*",
                    "semanasExamen.horaFin",
                    "semanasExamen.horaInicio"
                });
        model.addAttribute("jRolExamenes", jRolExamenes.toString());
        return this.index(model, session);
    }

    @ResponseBody
    @RequestMapping(value = "listGruposEspeciales", method = RequestMethod.GET)
    public DynatableResponse listGruposEspeciales(DynatableFilter filter, @RequestParam("rolexamenes") Long idRolExamenes, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<SeccionGrupoEspecial> list = grupoEspecialService.allSeccionesGrupoEspecialByRolExamenes(filter, new RolExamenes(idRolExamenes));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (SeccionGrupoEspecial item : list) {
            ObjectNode jItem = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "seccion.*",
                "seccion.grupoHoras.*",
                "aula.*",
                "rolExamenes.*",
                "userRegistro.*",
                "userRegistro.persona.*",
                "docente.persona.apellidosNombres",
                "grupoHorasExamen.*",
                "grupoHorasExamen.dia.*",
                "grupoHorasExamen.horaInicio.*",
                "grupoHorasExamen.horaFin.*",
                "grupoHorasExamen.semanaExamen.id",
                "grupoHorasExamen.semanaExamen.numeroSemana",
                "grupoHorasExamen.grupoHoras.letra",
                "grupoHorasExamen.grupoHoras.codigo"
            });
            jItem.put("alumnosEspecialesActivosCount", item.getAlumnosEspecialesActivosCount());
            array.add(jItem);
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "calcularGrupoEspecial", method = RequestMethod.POST)
    public JsonResponse calcularGrupoEspecial(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            grupoEspecialService.calcularExamenesGrupoEspecial(rolExamenes, ds);
            response.setMessage("Grupo especial calculado corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "listAlumnosGrupoEspecial", method = RequestMethod.GET)
    public DynatableResponse listAlumnosGrupoEspecial(DynatableFilter filter, @RequestParam("seccionGrupoEspecial") Long seccionGrupoEspecial, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<AlumnoGrupoEspecial> list = grupoEspecialService.allAlumnosGrupoEspecialDynaBySecGpoEsp(filter, new SeccionGrupoEspecial(seccionGrupoEspecial));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoGrupoEspecial item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "alumno.*",
                "alumno.persona.*"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "{tipoAccion}/excluir", method = RequestMethod.POST)
    public JsonResponse excluir(
            @PathVariable("tipoAccion") String tipoAccion,
            @RequestBody ObjectNode objeto,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (GrupoEspecialController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionGrupoEspecial seccionGrupoEspecial = (SeccionGrupoEspecial) mapper.readValue(objeto.toString(), SeccionGrupoEspecial.class);
                grupoEspecialService.excluirSeccionEspecial(seccionGrupoEspecial, ds);
            } else if (GrupoEspecialController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoGrupoEspecial alumnoGrupoEspecial = (AlumnoGrupoEspecial) mapper.readValue(objeto.toString(), AlumnoGrupoEspecial.class);
                grupoEspecialService.excluirAlumnoEspecial(alumnoGrupoEspecial, ds);
            }

            response.setMessage("Excluido corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{tipoAccion}/incluir", method = RequestMethod.POST)
    public JsonResponse incluir(
            @PathVariable("tipoAccion") String tipoAccion,
            @RequestBody ObjectNode objeto,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (GrupoEspecialController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionGrupoEspecial seccionGrupoEspecial = (SeccionGrupoEspecial) mapper.readValue(objeto.toString(), SeccionGrupoEspecial.class);
                grupoEspecialService.activarSeccionEspecial(seccionGrupoEspecial, ds);
            } else if (GrupoEspecialController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoGrupoEspecial alumnoGrupoEspecial = (AlumnoGrupoEspecial) mapper.readValue(objeto.toString(), AlumnoGrupoEspecial.class);
                grupoEspecialService.activarAlumnoEspecial(alumnoGrupoEspecial, ds);
            }

            response.setMessage("Incluido corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
