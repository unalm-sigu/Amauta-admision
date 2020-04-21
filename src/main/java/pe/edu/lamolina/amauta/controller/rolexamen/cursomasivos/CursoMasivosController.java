package pe.edu.lamolina.amauta.controller.rolexamen.cursomasivos;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.amauta.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/cursomasivos")
public class CursoMasivosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoMasivosService service;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    private enum TipoAccion {
        CURSO,
        SECCION,
        DOCENTE,
        ALUMNO
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolexamenes = service.allRolExamenesByCicloActivo(ds.getCicloAcademico());
        ArrayNode jRolexamenes = new ArrayNode(JsonNodeFactory.instance);
        for (RolExamenes rolexamen : rolexamenes) {
            ObjectNode rolExam = JsonHelper.createJson(rolexamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "situacionConfigurarCursoMasivo",
                        "eventoCicloAcademico.cicloAcademico.descripcion",
                        "eventoCicloAcademico.fechaInicio", "eventoCicloAcademico.fechaFin",
                        "nombre", "estado", "fechaPublicacion"
                    });

            jRolexamenes.add(rolExam);
        }

        model.addAttribute("jRolexamenes", jRolexamenes.toString());

        return "rolexamen/cursomasivos/cursomasivos";
    }

    @RequestMapping("{rolExamen}")
    public String indexWithRolExamen(
            @PathVariable("rolExamen") Long rolExamenId,
            Model model,
            HttpSession session) {

        RolExamenes rolExamenes = service.findRolExamenes(new RolExamenes(rolExamenId));
        ObjectNode jRolExamenes = JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false,
                new String[]{
                    "*",
                    "situacionConfigurarCursoMasivo",
                    "eventoCicloAcademico.eventoAcademico.*",
                    "semanasExamen.rolExamenes.*",
                    "semanasExamen.*",
                    "semanasExamen.horaFin",
                    "semanasExamen.horaInicio"
                });
        model.addAttribute("jRolExamenes", jRolExamenes.toString());
        return this.index(model, session);
    }

    @RequestMapping("secciones/{cursoMasivo}")
    public String secciones(@PathVariable("cursoMasivo") Long idCursoMasivo, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        JsonResponse cursoMasivoExamen = this.findCursoMasivo(model, new CursoMasivoExamen(idCursoMasivo), session);
        model.addAttribute("jCursoMasivoExamen", cursoMasivoExamen.getData().toString());
        return "rolexamen/cursomasivos/cursoMasivoSecciones";
    }

    @ResponseBody
    @RequestMapping("findCursoMasivo")
    public JsonResponse findCursoMasivo(Model model, @RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CursoMasivoExamen cursoMasivoExamen = service.findCursoMasivo(cursoMasivosExamen.getId());
            model.addAttribute("cursoMasivoExamen", cursoMasivoExamen);

            response.setData(JsonHelper.createJson(cursoMasivoExamen, jsonFactory, true,
                    new String[]{
                        "*",
                        "rolExamenes.*",
                        "curso.id",
                        "curso.codigo",
                        "curso.nombre",
                        "curso.tpc",
                        "grupoHorasExamen.*",
                        "grupoHorasExamen.horaInicio.*",
                        "grupoHorasExamen.horaFin.*",
                        "grupoHorasExamen.grupoHoras.*"
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
    @RequestMapping("save")
    public JsonResponse save(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(cursoMasivosExamen, ds.getCicloAcademico(), ds.getUsuario());
            response.setSuccess(true);
            response.setMessage("Curso guardado satisfactoriamnente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveAulas")
    public JsonResponse saveAulas(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveAula(cursoMasivosExamen, ds.getCicloAcademico(), ds);
            response.setSuccess(true);
            response.setMessage("Aula guardado satisfactoriamnente");

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
    @RequestMapping("list")
    public JsonResponse list(@RequestBody RolExamenes rolExamenes, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            List<CursoMasivoExamen> cursoMasivosByRolExamenes = service.listCursosMasivosExamenes(rolExamenes);

            ArrayNode jCursoMasivosByRolExamen = new ArrayNode(JsonNodeFactory.instance);
            for (CursoMasivoExamen cursoMasivoByRolExamen : cursoMasivosByRolExamenes) {

                ObjectNode cursoMasivo = JsonHelper.createJson(cursoMasivoByRolExamen, JsonNodeFactory.instance, true,
                        new String[]{
                            "*",
                            "curso.id",
                            "curso.codigo",
                            "curso.nombre",
                            "curso.tpc",
                            "aulasCursosMasivos.aula.*",
                            "grupoHorasExamen.id",
                            "grupoHorasExamen.fecha",
                            "grupoHorasExamen.dia.*",
                            "grupoHorasExamen.horaInicio.*",
                            "grupoHorasExamen.horaFin.*",
                            "grupoHorasExamen.semanaExamen.id",
                            "grupoHorasExamen.semanaExamen.numeroSemana",
                            "grupoHorasExamen.grupoHoras.letra",
                            "grupoHorasExamen.grupoHoras.codigo",
                            "rolExamenes.id"});

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
    @RequestMapping("{idRolExamen}/loadCurso")
    public JsonResponse loadCurso(
            @RequestParam("nombre") String nombre,
            @PathVariable("idRolExamen") Long idRolExamen,
            HttpSession session,
            HttpServletRequest request) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Curso> cursos = service.allCursosByCiclo(nombre, new RolExamenes(idRolExamen), ds.getCicloAcademico());
            ArrayNode jCursos = new ArrayNode(JsonNodeFactory.instance);
            for (Curso curso : cursos) {
                ObjectNode cur = JsonHelper.createJson(curso, JsonNodeFactory.instance, false,
                        new String[]{
                            "*",
                            "departamentoAcademico.*"
                        });
                jCursos.add(cur);
            }
            response.setData(jCursos);

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allModulos")
    public JsonResponse allModulos(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> pabellones = service.allPabellonesByOficina();

            ArrayNode arrayPabellones = new ArrayNode(jsonFactory);
            for (Aula pabellon : pabellones) {
                ObjectNode json = createPabellonesJson(pabellon);
                arrayPabellones.add(json);
            }

            response.setData(arrayPabellones);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createPabellonesJson(Aula pabellon) {
        ObjectNode json = JsonHelper.createJson(pabellon, JsonNodeFactory.instance, true, new String[]{
            "id",
            "codigo",
            "nombre"
        });
        return json;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(CursoMasivoExamen cursoMasivoExamen, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.eliminarCursoMasivoExamen(cursoMasivoExamen, ds);

            response.setMessage("Curso eliminado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allAulasModulo")
    public JsonResponse allAulasModulo(@RequestBody Aula modulo, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> aulas = service.allAulasOERAByModulo(modulo);

            ArrayNode arrayAulas = new ArrayNode(jsonFactory);
            for (Aula aula : aulas) {
                ObjectNode json = createAulasJson(aula);
                arrayAulas.add(json);
            }

            response.setData(arrayAulas);
            response.setSuccess(true);

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (CursoMasivosController.TipoAccion.CURSO.name().equals(tipoAccion)) {
                CursoMasivoExamen cursoMasivoExamen = (CursoMasivoExamen) mapper.readValue(objeto.toString(), CursoMasivoExamen.class);
                service.excluirCursoMasivo(cursoMasivoExamen, ds);
            } else if (CursoMasivosController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionCursoMasivo seccionCursoMasivo = (SeccionCursoMasivo) mapper.readValue(objeto.toString(), SeccionCursoMasivo.class);
                service.excluirSeccionCursoMasivo(seccionCursoMasivo, null, ds);
            } else if (CursoMasivosController.TipoAccion.DOCENTE.name().equals(tipoAccion)) {
                DocenteCursoMasivo docenteCursoMasivo = (DocenteCursoMasivo) mapper.readValue(objeto.toString(), DocenteCursoMasivo.class);
                service.excluirDocenteCursoMasivo(docenteCursoMasivo, ds);
            } else if (CursoMasivosController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoCursoMasivo alumnoCursoMasivo = (AlumnoCursoMasivo) mapper.readValue(objeto.toString(), AlumnoCursoMasivo.class);
                service.excluirAlumnoCursoMasivo(alumnoCursoMasivo, ds);
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

            if (CursoMasivosController.TipoAccion.CURSO.name().equals(tipoAccion)) {
                CursoMasivoExamen cursoMasivoExamen = (CursoMasivoExamen) mapper.readValue(objeto.toString(), CursoMasivoExamen.class);
                service.activarCursoMasivo(cursoMasivoExamen, ds);
            } else if (CursoMasivosController.TipoAccion.SECCION.name().equals(tipoAccion)) {
                SeccionCursoMasivo seccionCursoMasivo = (SeccionCursoMasivo) mapper.readValue(objeto.toString(), SeccionCursoMasivo.class);
                service.activarSeccionCursoMasivo(seccionCursoMasivo, ds);
            } else if (CursoMasivosController.TipoAccion.DOCENTE.name().equals(tipoAccion)) {
                DocenteCursoMasivo docenteCursoMasivo = (DocenteCursoMasivo) mapper.readValue(objeto.toString(), DocenteCursoMasivo.class);
                service.activarDocenteCursoMasivo(docenteCursoMasivo, ds);
            } else if (CursoMasivosController.TipoAccion.ALUMNO.name().equals(tipoAccion)) {
                AlumnoCursoMasivo alumnoCursoMasivo = (AlumnoCursoMasivo) mapper.readValue(objeto.toString(), AlumnoCursoMasivo.class);
                service.activarAlumnoCursoMasivo(alumnoCursoMasivo, ds);
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
    @RequestMapping(value = "eliminarCursosMasivos", method = RequestMethod.POST)
    public JsonResponse eliminarCursosMasivos(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            service.eliminarCursosMasivos(rolExamenes);
            response.setMessage("Cursos especiales eliminados corretamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "saveHorarioExamen", method = RequestMethod.POST)
    public JsonResponse saveHorarioExamen(
            @RequestBody CursoMasivoExamen cursoMasivoExamen,
            HttpSession session, HttpServletRequest request) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            logger.debug("saveHorarioExamen");
            service.saveHorarioExamen(cursoMasivoExamen, ds);
            response.setMessage("Horario guardado correctamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            if (rolExamenesLogger.getLogDetails() != null && !rolExamenesLogger.getLogDetails().isEmpty()) {
                JsonNodeFactory jc = JsonNodeFactory.instance;
                ObjectNode jLog = JsonHelper.createJson(rolExamenesLogger, jc, false,
                        new String[]{
                            "*",
                            "logDetails.*"
                        });
                response.setData(jLog);
            }
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "revisarGpoHorasExamenCursoMasivo", method = RequestMethod.POST)
    public JsonResponse revisarGpoHorasExamenCursoMasivo(
            @RequestBody CursoMasivoExamen cursoMasivoExamen,
            HttpSession session, HttpServletRequest request) {

        JsonResponse response = new JsonResponse();
        JsonNodeFactory jc = JsonNodeFactory.instance;

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());

            logger.debug("revisarGpoHorasExamenCursoMasivo");
            GrupoHorasExamen gpoHorasExamen = service.revisarGpoHorasExamenCursoMasivo(cursoMasivoExamen, ds);
            ObjectNode gpoJson = JsonHelper.createJson(gpoHorasExamen, JsonNodeFactory.instance, true, new String[]{"*"});

            ObjectNode node = new ObjectNode(jc);
            node.set("grupoHorasExamen", gpoJson);
            if (rolExamenesLogger.getLogDetails() != null && !rolExamenesLogger.getLogDetails().isEmpty()) {
                ObjectNode jLog = JsonHelper.createJson(rolExamenesLogger, jc, false, new String[]{"*", "logDetails.*"});
                node.set("conflictos", jLog);
            }

            response.setData(node);
            response.setMessage("Revisión de horarios finalizada correctamente.");
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
    @RequestMapping(value = "listDocentesCursosMasivos", method = RequestMethod.GET)
    public DynatableResponse listDocentesCursosMasivos(DynatableFilter filter, @RequestParam("cursoMasivo") Long idCursoMasivo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<DocenteCursoMasivo> list = service.allDocentesCursosMasivosDynaByCursoMasivo(filter, new CursoMasivoExamen(idCursoMasivo));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (DocenteCursoMasivo item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "docente.codigo",
                "docente.persona.apellidosNombres"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "listAlumnosCursosMasivos", method = RequestMethod.GET)
    public DynatableResponse listAlumnosCursosMasivos(DynatableFilter filter, @RequestParam("cursoMasivo") Long idCursoMasivo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<AlumnoCursoMasivo> list = service.allAlumnosCursoMasivosDynaByCursoMasivo(filter, new CursoMasivoExamen(idCursoMasivo));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoCursoMasivo item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "alumno.codigo",
                "alumno.persona.apellidosNombres"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping(value = "listSeccionesCursosMasivos", method = RequestMethod.GET)
    public DynatableResponse listSeccionesCursosMasivos(DynatableFilter filter, @RequestParam("cursoMasivo") Long idCursoMasivo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DynatableResponse json = new DynatableResponse();

        List<SeccionCursoMasivo> list = service.allSeccionesCursoMasivosDynaByCursoMasivo(filter, new CursoMasivoExamen(idCursoMasivo));
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (SeccionCursoMasivo item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "seccion.id",
                "seccion.codigo2",
                "seccion.grupoHoras.id",
                "seccion.grupoHoras.codigo",
                "seccion.aula.codigo"
            }));
        }

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
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
    @RequestMapping("allModulosVerificados")
    public JsonResponse allModulosVerificados(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> pabellones = service.allPabellonesByOficina();

            ArrayNode arrayPabellones = new ArrayNode(jsonFactory);
            for (Aula pabellon : pabellones) {
                ObjectNode json = createPabellonesJson(pabellon);
                arrayPabellones.add(json);
            }

            response.setData(arrayPabellones);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allAulasVerificadasByModulo")
    public JsonResponse allAulasVerificadasByModulo(@RequestBody Aula modulo, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> aulas = service.allAulasVerificadasByModulo(modulo);

            ArrayNode arrayAulas = new ArrayNode(jsonFactory);
            for (Aula aula : aulas) {
                ObjectNode json = createAulasJson(aula);
                List<String> observaciones = aula.getObservaciones();
                ArrayNode observacionesJson = new ArrayNode(jsonFactory);
                for (String observa : observaciones) {
                    observacionesJson.add(observa);
                }
                json.set("observaciones", observacionesJson);
                arrayAulas.add(json);
            }

            response.setData(arrayAulas);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cmbiarCambioAulasGrupo")
    public JsonResponse cmbiarCambioAulasGrupo(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<String> restricciones = service.cambiarAulasGrupoForCursoMasivo(cursoMasivosExamen, ds.getCicloAcademico(), ds);
            response.setMessage("Curso masivo modificado satisfactoriamente");
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
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }
    @ResponseBody
    @RequestMapping("cmbiarCambioAulasGrupoForzado")
    public JsonResponse cmbiarCambioAulasGrupoForzado(@RequestBody CursoMasivoExamen cursoMasivosExamen, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<String> restricciones = service.cambiarCambioAulasGrupoForzado(cursoMasivosExamen, ds.getCicloAcademico(), ds);
            response.setMessage("Curso masivo modificado satisfactoriamente");
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
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("quitarGrupo")
    public JsonResponse quitarGrupo(@RequestBody CursoMasivoExamen cursoMasivo,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.removerHorario(cursoMasivo, ds);

            response.setMessage("Horario retirado correctamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createAulasJson(Aula aula) {
        ObjectNode json = JsonHelper.createJson(aula, JsonNodeFactory.instance, true, new String[]{"*", "observaciones"});
        return json;
    }
}
