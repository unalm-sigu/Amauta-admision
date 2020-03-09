package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.CAM_NOTA;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.RCI;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.REI;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.resolucion.ResolucionService;
import pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes.ResolucionExistenteService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/tramitecondicional")
public class TramiteCondicionalController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteCondicionalService service;

    @Autowired
    ResolucionExistenteService existenteService;

    @Autowired
    MatriculableService matriculableService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    ResolucionService resolucionService;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Autowired
    PromedioService promedioService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.allCiclos(ds.getCicloAcademico());

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode tipoTramiteJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode oficinasJson = new ArrayNode(JsonNodeFactory.instance);

        List<TipoTramite> tipoTramite = service.allTipoTramite();
        for (TipoTramite tipo : tipoTramite) {
            if (Arrays.asList(RCI.name(), REI.name(), CAM_NOTA.name()).contains(tipo.getCodigo())) {
                tipoTramiteJson.add(JsonHelper.createJson(tipo, JsonNodeFactory.instance, new String[]{"*"}));
            }
        }
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arrayNode.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{
                "*"}));
        }
        List<Oficina> oficinas = resolucionService.allOFicinasByUser(ds);
        for (Oficina oficina : oficinas) {
            ObjectNode oficinaJson = JsonHelper.createJson(oficina, JsonNodeFactory.instance, new String[]{"*"});
            oficinasJson.add(oficinaJson);
        }
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tipoTramite", tipoTramiteJson);
        model.addAttribute("ciclos", arrayNode);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        return "academico/tramiteCondicional/tramiteCondicional";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<Tramite> tramites = service.allByCiclo(ds.getCicloAcademico(), filter);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (Tramite tramite : tramites) {
                arrayNode.add(JsonHelper.createJson(tramite, JsonNodeFactory.instance, new String[]{
                    "*",
                    "tipoTramite.*",
                    "cicloAcademicoResolucion.*",
                    "cursoResolucion.*",
                    "alumno.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",}));
            }
            json.setData(arrayNode);
            json.setFiltered(filter.getFiltered());
            json.setTotal(10);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(
            @RequestBody Tramite tramite,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (tramite.getTipoTramite().getCodigo().equals(RCI.name())) {
                service.saveRetiroCiclo(tramite, ds);
            } else if (tramite.getTipoTramite().getCodigo().equals(REI.name())) {
                service.saveReincorporacion(tramite, ds);
            } else if (tramite.getTipoTramite().getCodigo().equals(CAM_NOTA.name())) {
                service.saveCambioNota(tramite, ds);
            }

            promedioService.calcularSituacionAcademica(tramite.getAlumno(), ds);
            String token = matriculableService.saveMatriculable(tramite.getAlumno(), ds.getCicloAcademico(), ds);
            matriculableService.generarAportes(ds, token);
            response.setMessage("Se guardó satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(
            @RequestBody Tramite tramite,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            MatriculaResumen matriculaResumen = new MatriculaResumen();
            String token = "";

            if (tramite.getTipoTramite().getCodigo().equals(TipoTramiteEnum.RCI.name())) {
                token = service.updateRetiroCiclo(tramite, ds);
            } else if (tramite.getTipoTramite().getCodigo().equals(TipoTramiteEnum.REI.name())) {
                token = service.updateReincorporacion(tramite, ds);
            } else if (tramite.getTipoTramite().getCodigo().equals(TipoTramiteEnum.CAM_NOTA.name())) {
                token = service.updateCambioNota(tramite, ds);
            }

            matriculableService.calcularPromedios(token, ds);
            matriculableService.revisarCurriculaAlumnos(ds, token);
            matriculableService.revisarMatriculables(ds, token);

            response.setData(JsonHelper.createJson(matriculaResumen, jsonFactory, new String[]{"id"}));
            response.setMessage("Se actualizó satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("verificarEliminarMat")
    public JsonResponse evaluarEliminarMatriculable(
            @RequestBody Tramite tramite,
            Model model,
            HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.evaluarEliminarMatriculable(tramite.getAlumno(), ds.getCicloAcademico(), ds);

            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allAlumnoByNombre")
    public JsonResponse allAlumnoByNombre(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Alumno> lista = service.allAlumnoByNombre(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Alumno alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "id",
                            "id",
                            "codigo",
                            "modalidadEstudio.nombre",
                            "carrera.codigo",
                            "carrera.nombre",
                            "carrera.facultad.codigo",
                            "carrera.facultad.nombre",
                            "persona.numeroDocIdentidad",
                            "persona.apellidosNombres",
                            "persona.nombreCompleto",
                            "persona.rutaFoto",
                            "persona.tipoDocumento.*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCursosAlumnoByName")
    public JsonResponse allCursosAlumnoByName(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "idAlumno", required = true) Long idAlumno,
            @RequestParam(value = "idCiclo", required = true) Long idCiclo,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Curso> lista = service.allCursosByName(nombre, new Alumno(idAlumno), new CicloAcademico(idCiclo), ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Curso alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                            "*"}));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
