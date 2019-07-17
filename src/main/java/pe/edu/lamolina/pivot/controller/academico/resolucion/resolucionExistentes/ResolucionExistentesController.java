package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.CAM_NOTA;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.CURDIR;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.RCI;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.REIC;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.resolucion.ResolucionService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/resolucion")
public class ResolucionExistentesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionExistenteService service;

    @Autowired
    ResolucionService resolucionService;

    @Autowired
    MatriculableService matriculableService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    private MultipartFile resolucionFile;

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

    @RequestMapping(value = "resolucionExistentes", method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ArrayNode oficinasJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode ciclosJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode tipoResolucionJson = new ArrayNode(JsonNodeFactory.instance);

        List<TipoResolucion> tipoResolucions = service.allTipoResolucion();
        for (TipoResolucion tipoResolucion : tipoResolucions) {
            if (Arrays.asList(RCI.name(), REIC.name(), CAM_NOTA.name(), CURDIR.name()).contains(tipoResolucion.getCodigo())) {
                tipoResolucionJson.add(JsonHelper.createJson(tipoResolucion, JsonNodeFactory.instance, new String[]{"*"}));
            }
        }

        List<CicloAcademico> cicloAcademicos = service.ciclosAnteriores(5);
        List<Oficina> oficinas = resolucionService.allOFicinasByUser(ds);
        for (Oficina oficina : oficinas) {
            ObjectNode oficinaJson = JsonHelper.createJson(oficina, JsonNodeFactory.instance, new String[]{"*"});
            oficinasJson.add(oficinaJson);
        }
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            ObjectNode cicloJson = JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{"*"});
            ciclosJson.add(cicloJson);
        }
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);
        return "academico/resolucion/resolucionexistentes/resolucionExistentes";
    }

    @ResponseBody
    @RequestMapping("findAlumno")
    public JsonResponse findAlumno(
            @RequestParam("nombre") String nombre,
            @RequestParam("instanciaOficina") Long instanciaOficina,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<Alumno> alumnos = service.allAlumnoByOficina(nombre, instanciaOficina);
            for (Alumno alumno : alumnos) {
                data.add(JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
                    "id",
                    "codigo",
                    "persona.nombreCompleto",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.*",
                    "carrera.facultad.*",}));
            }
            response.setSuccess(Boolean.TRUE);
            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Resolucion resolucion,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);
            if (resolucion.getTipoResolucion().getCodigo().equals(REIC.name())) {
                List<Alumno> alumnos = service.saveReincorporacion(resolucion, ds.getUsuario(), ds);
                for (Alumno alumno : alumnos) {
                    matriculableService.revisarSituacionAcademica(alumno, ds);
                    matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.REI.name(), ds);
                }
            } else if (resolucion.getTipoResolucion().getCodigo().equals(RCI.name())) {
                List<Alumno> alumnos = service.saveRetiroCiclo(resolucion, ds.getUsuario(), ds);
                for (Alumno alumno : alumnos) {
                    matriculableService.revisarSituacionAcademica(alumno, ds);
                    matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.RETIRO_CICLO.name(), ds);
                }
            } else if (resolucion.getTipoResolucion().getCodigo().equals(CAM_NOTA.name())) {
                List<Alumno> alumnos = service.saveCambioNota(resolucion, ds.getUsuario(), ds);
                for (Alumno alumno : alumnos) {
                    matriculableService.revisarSituacionAcademica(alumno, ds);
                    matriculableService.saveMatriculable(alumno, TipoCondicionalEnum.CAMBIO_NOTA.name(), ds);
                }
            } else {
                service.saveCursoDirigido(resolucion, ds.getUsuario(), ds);
            }

            response.setMessage("Se realizó el registro satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("alumnos/{idResolucion}")
    public JsonResponse alumnos(@PathVariable(value = "idResolucion") Long resolucion,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            Resolucion resolucionDB = service.findByResolucion(resolucion, ds);
            List<Reincorporacion> reincorporacions = new ArrayList<>();
            List<RetiroCiclo> retiroCiclos = new ArrayList<>();
            List<CambioNota> cambioNotas = new ArrayList<>();
            List<CursoDirigido> cursoDirigidos = new ArrayList<>();
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            if (resolucionDB.getTipoResolucion().getCodigo().equals(REIC.name())) {
                reincorporacions = service.allReincorporacionByResolucion(resolucionDB);
                for (Reincorporacion reicorporacion : reincorporacions) {
                    objectNode = JsonHelper.createJson(reicorporacion, JsonNodeFactory.instance, new String[]{
                        "*",
                        "facultad.*",
                        "alumno.*",
                        "alumno.persona.*",
                        "alumno.persona.tipoDocumento.*",
                        "cicloReincorporacion.*"
                    });
                    objectNode.put("tipo", REIC.name());
                    array.add(objectNode);
                }
            } else if (resolucionDB.getTipoResolucion().getCodigo().equals(RCI.name())) {
                retiroCiclos = service.allRetiroCicloByResolucion(resolucionDB);
                for (RetiroCiclo retiroCiclo : retiroCiclos) {
                    objectNode = JsonHelper.createJson(retiroCiclo, JsonNodeFactory.instance, new String[]{
                        "*",
                        "alumno.*",
                        "alumno.persona.*",
                        "alumno.persona.tipoDocumento.*",
                        "cicloAcademico.*"
                    });
                    objectNode.put("tipo", RCI.name());
                    array.add(objectNode);
                }
            } else if (resolucionDB.getTipoResolucion().getCodigo().equals(CAM_NOTA.name())) {
                cambioNotas = service.allCambioNota(resolucionDB);
                for (CambioNota cambioNota : cambioNotas) {
                    objectNode = JsonHelper.createJson(cambioNota, JsonNodeFactory.instance, new String[]{
                        "*",
                        "curso.*",
                        "alumno.*",
                        "alumno.persona.*",
                        "alumno.persona.tipoDocumento.*",
                        "cicloAcademico.*"
                    });
                    objectNode.put("tipo", CAM_NOTA.name());
                    array.add(objectNode);
                }
            } else if (resolucionDB.getTipoResolucion().getCodigo().equals(CURDIR.name())) {
                cursoDirigidos = service.allCursodirigido(resolucionDB);
                for (CursoDirigido cursoDir : cursoDirigidos) {
                    objectNode = JsonHelper.createJson(cursoDir, JsonNodeFactory.instance, new String[]{
                        "*",
                        "curso.*",
                        "tramite.alumno.*",
                        "tramite.alumno.persona.*",
                        "tramite.alumno.persona.tipoDocumento.*", //                        "cicloAcademico.*"
                    });
                    objectNode.put("tipo", CURDIR.name());
                    array.add(objectNode);
                }
            }
            response.setSuccess(Boolean.TRUE);
            response.setData(array);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, e.getLocalizedMessage());
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
