package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/gruporegular")
public class GrupoRegularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoRegularService grupoRegularService;

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

        List<RolExamenes> rolesExamenes = grupoRegularService.allRolExamenesActives(ds.getCicloAcademico());

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
        return "rolexamen/gruporegular/grupoRegular";
    }

    @ResponseBody
    @RequestMapping(value = "calcularGruposRegulares", method = RequestMethod.POST)
    public JsonResponse calcularGruposRegulares(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            grupoRegularService.calcularExamenesGrupoRegular(rolExamenes, ds.getCicloAcademico(), ds);
            response.setMessage("Grupos regulares calculados corretamente.");
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
    @RequestMapping(value = "listGruposRegulares", method = RequestMethod.POST)
    public JsonResponse listGruposRegulares(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<LetraGrupoRegular> letrasGruposRegulares = grupoRegularService.listGruposRegulares(rolExamenes);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            ArrayNode jLetrasGruposRegulares = new ArrayNode(jc);
            for (LetraGrupoRegular letrasGruposRegulare : letrasGruposRegulares) {
                jLetrasGruposRegulares.add(JsonHelper.createJson(letrasGruposRegulare, jc, false,
                        new String[]{
                            "*",
                            "userRegistro.*",
                            "userRegistro.persona.apellidosNombres",
                            "grupoHorasExamen.*",
                            "grupoHorasExamen.dia.*",
                            "grupoHorasExamen.horaInicio.*",
                            "grupoHorasExamen.horaFin.*",
                            "grupoHorasExamen.semanaExamen.id",
                            "grupoHorasExamen.semanaExamen.numeroSemana",
                            "grupoHorasExamen.grupoHoras.letra",
                            "grupoHorasExamen.grupoHoras.codigo"
                        }));
            }
            response.setData(jLetrasGruposRegulares);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{tipoAccion}/loadLetraGrupoRegularInfo", method = RequestMethod.POST)
    public JsonResponse loadLetraGrupoRegularInfo(
            @PathVariable("tipoAccion") String tipoAccion,
            @RequestBody LetraGrupoRegular letraGrupoRegular,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            if (TipoAccion.GRUPO.name().equals(tipoAccion)) {
                List<GrupoRegularExamen> grupos = grupoRegularService.allGruposRegularExamenByLetraGrupoRegular(letraGrupoRegular);
                ArrayNode jGrupos = new ArrayNode(jc);
                grupos.forEach(x -> {
                    jGrupos.add(JsonHelper.createJson(x, jc, false,
                            new String[]{
                                "*",
                                "grupoHoras.codigo",
                                "grupoHoras.letra",}));
                });
                response.setData(jGrupos);
            } else if (TipoAccion.SECCION.name().equals(tipoAccion)) {
                ArrayNode jSecciones = new ArrayNode(jc);
                List<SeccionGrupoRegular> secciones = grupoRegularService.allSeccionesGrupoRegularExamenByLetraGrupoRegular(letraGrupoRegular);
                secciones.forEach(x -> {
                    jSecciones.add(JsonHelper.createJson(x, jc, false,
                            new String[]{
                                "*",
                                "seccion.codigo",
                                "seccion.codigo2",
                                "aula.*",
                                "docente.persona.apellidosNombres"
                            }));
                });
                response.setData(jSecciones);
            } else if (TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                ArrayNode jAlumnos = new ArrayNode(jc);
                List<AlumnoGrupoRegular> alumnos = grupoRegularService.allAlumnosGrupoRegularByLetraGrupoRegular(letraGrupoRegular);
                alumnos.forEach(x -> {
                    jAlumnos.add(JsonHelper.createJson(x, jc, false,
                            new String[]{
                                "*",
                                "alumno.codigo",
                                "alumno.persona.apellidosNombres"
                            }));
                });
                response.setData(jAlumnos);
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

            if (TipoAccion.GRUPO.name().equals(tipoAccion)) {
                GrupoRegularExamen grupoRegularExamen = (GrupoRegularExamen) mapper.readValue(objeto.toString(), GrupoRegularExamen.class);
                grupoRegularService.excluirGrupoRegular(grupoRegularExamen, ds);
            } else if (TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionGrupoRegular seccionGrupoRegular = (SeccionGrupoRegular) mapper.readValue(objeto.toString(), SeccionGrupoRegular.class);
                grupoRegularService.excluirGrupoRegular(seccionGrupoRegular, ds);
            } else if (TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoGrupoRegular alumnoRegularExamen = (AlumnoGrupoRegular) mapper.readValue(objeto.toString(), AlumnoGrupoRegular.class);
                grupoRegularService.excluirGrupoRegular(alumnoRegularExamen, ds);
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

}
