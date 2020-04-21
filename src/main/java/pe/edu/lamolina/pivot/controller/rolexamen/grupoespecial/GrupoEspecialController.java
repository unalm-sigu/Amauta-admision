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
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/grupoespecial")
public class GrupoEspecialController {

    @Autowired
    GrupoEspecialService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = service.allRolExamenesActives(ds.getCicloAcademico());
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
        model.addAttribute("jRolExamenes", jRolExamenes.toString());
        return this.index(model, session);
    }

    @ResponseBody
    @RequestMapping("listGruposEspeciales")
    public DynatableResponse listGruposEspeciales(
            DynatableFilter filter,
            @RequestParam("rolexamenes") Long idRolExamenes,
            @RequestParam("incompletos") Long incompletos,
            HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<SeccionGrupoEspecial> list = service.allSeccionesGrupoEspecialByRolExamenes(filter, new RolExamenes(idRolExamenes), incompletos);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (SeccionGrupoEspecial item : list) {
            ObjectNode jItem = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "seccion.id",
                "seccion.codigo2",
                "seccion.matriculados",
                "seccion.grupoHoras.id",
                "seccion.grupoHoras.codigo",
                "seccion.grupoSeccion.id",
                "seccion.grupoSeccion.curso.id",
                "seccion.grupoSeccion.curso.nombre",
                "seccion.grupoSeccion.curso.codigo",
                "seccion.grupoSeccion.curso.tpc",
                "seccion.aula.id", "seccion.aula.codigo",
                "seccion.grupoHoras.id", "seccion.grupoHoras.codigo",
                "aula.*",
                "rolExamenes.*",
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            service.calcularExamenesGrupoEspecial(rolExamenes, ds);
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
    @RequestMapping(value = "limpiarExamenGrupoEspecial", method = RequestMethod.POST)
    public JsonResponse limpiarExamenGrupoEspecial(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            service.limpiarExamenGrupoEspecial(rolExamenes, ds);
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<AlumnoGrupoEspecial> list = service.allAlumnosGrupoEspecialDynaBySecGpoEsp(filter, new SeccionGrupoEspecial(seccionGrupoEspecial));
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (GrupoEspecialController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionGrupoEspecial seccionGrupoEspecial = (SeccionGrupoEspecial) mapper.readValue(objeto.toString(), SeccionGrupoEspecial.class);
                service.excluirSeccionEspecial(seccionGrupoEspecial, ds);
            } else if (GrupoEspecialController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoGrupoEspecial alumnoGrupoEspecial = (AlumnoGrupoEspecial) mapper.readValue(objeto.toString(), AlumnoGrupoEspecial.class);
                service.excluirAlumnoEspecial(alumnoGrupoEspecial, ds);
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (GrupoEspecialController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionGrupoEspecial seccionGrupoEspecial = (SeccionGrupoEspecial) mapper.readValue(objeto.toString(), SeccionGrupoEspecial.class);
                service.activarSeccionEspecial(seccionGrupoEspecial, ds);
            } else if (GrupoEspecialController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoGrupoEspecial alumnoGrupoEspecial = (AlumnoGrupoEspecial) mapper.readValue(objeto.toString(), AlumnoGrupoEspecial.class);
                service.activarAlumnoEspecial(alumnoGrupoEspecial, ds);
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

    @ResponseBody
    @RequestMapping("quitarAula")
    public JsonResponse quitarAula(@RequestBody SeccionGrupoEspecial grupoSpecial,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {

            service.removerAula(grupoSpecial);

            response.setMessage("Aula retirada correctamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("quitarGrupo")
    public JsonResponse quitarGrupo(@RequestBody SeccionGrupoEspecial grupoSpecial,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {

            service.removerGrupo(grupoSpecial);

            response.setMessage("Grupo retirado correctamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allGrupoHE")
    public JsonResponse allGrupoHE(@RequestBody RolExamenes rolExamenes, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jc);
            List<GrupoHorasExamen> grupos = service.allGrupoHoraExamenByRolExamenes(rolExamenes);
            for (GrupoHorasExamen grupo : grupos) {
                ObjectNode jGrupo = JsonHelper.createJson(grupo, JsonNodeFactory.instance, new String[]{
                    "id", "grupoHoras.id", "grupoHoras.codigo", "rolExamenes.id", "descripcion"
                });
                array.add(jGrupo);
            }
            response.setData(array);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarAulaGrupo")
    public JsonResponse cambiarAulaGrupo(@RequestBody SeccionGrupoEspecial grupoSpecial, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<String> restricciones = service.saveCambioAulaGrupo(grupoSpecial);

            response.setMessage("Grupo modificado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
            if (!restricciones.isEmpty()) {
                response.setMessage("Se presentaron inconvenientes para realizar los cambios");
                response.setSuccess(Boolean.FALSE);
            }
            response.setData(restricciones);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarAulaGrupoForzado")
    public JsonResponse cambiarAulaGrupoForzado(@RequestBody SeccionGrupoEspecial grupoSpecial, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<String> restricciones = service.saveCambioAulaGrupoForzardo(grupoSpecial);

            response.setMessage("Grupo modificado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
            if (!restricciones.isEmpty()) {
                response.setMessage("Se presentaron inconvenientes para realizar los cambios");
                response.setSuccess(Boolean.FALSE);
            }
            response.setData(restricciones);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("buscarSeccion")
    public JsonResponse buscarSeccion(@RequestBody SeccionGrupoEspecial grupoSpecial, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Seccion seccion = grupoSpecial.getSeccion();
            RolExamenes rolExamenes = grupoSpecial.getRolExamenes();
            Seccion seccionBD = service.findSeccionByRolExamenes(seccion, ds.getCicloAcademico(), rolExamenes);

            ObjectNode node = JsonHelper.createJson(seccionBD, JsonNodeFactory.instance, new String[]{
                "id", "codigo2", "matriculados",
                "grupoHoras.codigo",
                "docentePrincipal.codigo",
                "docentePrincipal.persona.apellidosNombres",
                "aula.codigo",
                "grupoSeccion.curso.codigo",
                "grupoSeccion.curso.nombre",
                "grupoSeccion.curso.tpc"});

            response.setMessage("Sección ubicada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
            response.setData(node);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("addSeccionNueva")
    public JsonResponse addSeccionNueva(@RequestBody SeccionGrupoEspecial grupoSpecial, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Seccion seccion = grupoSpecial.getSeccion();
            RolExamenes rolExamenes = grupoSpecial.getRolExamenes();
            service.addSeccionNueva(seccion, ds.getCicloAcademico(), rolExamenes, ds);

            response.setMessage("Sección añadida satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("crearCursoMasivo")
    public JsonResponse crearCursoMasivo(@RequestBody SeccionGrupoEspecial grupoSpecial, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.crearCursoMasivo(grupoSpecial, ds.getUsuario(), ds.getCicloAcademico());

            response.setMessage("Sección añadida satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
