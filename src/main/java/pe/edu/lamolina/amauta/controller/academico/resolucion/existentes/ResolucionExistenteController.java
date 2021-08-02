package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.ING_HIS;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.INTES;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.amauta.controller.academico.resolucion.ResolucionService;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.BACHI;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.CAMBIO_PLAN_CURRICULAR;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.CAM_NOTA;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.CURDIR;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.OBTE_GRADO;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.PRACTICAS;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.RCI;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.READMISION;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.REIC;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.TITUL;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.TRAS;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.TRAS_INT;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Controller
@RequestMapping("academico/resolucion/existentes")
public class ResolucionExistenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static List<String> TIPOS_RESOLUCIONES = Arrays.asList(
            TipoResolucionEnum.READMISION.name(),
            TipoResolucionEnum.CAMBIO_PLAN_CURRICULAR.name(),
            TipoResolucionEnum.TRAS_INT.name(),
            TipoResolucionEnum.RCI.name(),
            TipoResolucionEnum.ANCI.name(),
            TipoResolucionEnum.REIC.name(),
            TipoResolucionEnum.CAM_NOTA.name(),
            TipoResolucionEnum.CURDIR.name(),
            TipoResolucionEnum.TRAS.name(),
            TipoResolucionEnum.INTES.name(),
            TipoResolucionEnum.NOTA_BAJA.name(),
            TipoResolucionEnum.BACHI.name(),
            TipoResolucionEnum.TITUL.name(),
            TipoResolucionEnum.PRACTICAS.name()
    );

    @Autowired
    ResolucionExistenteService service;

    @Autowired
    ResolucionService resolucionService;

    @Autowired
    MatriculableService matriculableService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode tipoResolucionJson = JaneHelper
                .from(service.allTipoResolucionByCodigo(TIPOS_RESOLUCIONES))
                .array();

        ArrayNode oficinasJson = JaneHelper
                .from(resolucionService.allOFicinasByUser(ds))
                .array();

        ArrayNode ciclosJson = JaneHelper
                .from(service.ciclosAnteriores(40))
                .array();

        ArrayNode carrerasJson = JaneHelper
                .from(service.allCarrera())
                .array();

        model.addAttribute("carreras", carrerasJson);
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);

        return "academico/resolucion/resolucionexistentes/resolucionExistentes";
    }

    @RequestMapping(value = "/{idResolucion}", method = RequestMethod.GET)
    public String updateresolucionExistentes(@PathVariable(value = "idResolucion") Long idResolucion, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode tipoResolucionJson = JaneHelper
                .from(service.allTipoResolucionByCodigo(TIPOS_RESOLUCIONES))
                .array();

        ArrayNode oficinasJson = JaneHelper
                .from(resolucionService.allOFicinasByUser(ds))
                .array();

        ArrayNode ciclosJson = JaneHelper
                .from(service.ciclosAnteriores(40))
                .array();

        ArrayNode carrerasJson = JaneHelper
                .from(service.allCarrera())
                .array();

        Resolucion resolucionDB = service.findByResolucion(idResolucion, ds);

        ObjectNode objectNode = this.findDataResolucion(resolucionDB);

        model.addAttribute("carreras", carrerasJson);
        model.addAttribute("oficinas", oficinasJson);
        model.addAttribute("tiposResolucion", tipoResolucionJson);
        model.addAttribute("ciclos", ciclosJson);
        model.addAttribute("resolucion", objectNode);

        return "academico/resolucion/resolucionexistentes/resolucionExistentes";
    }

    @ResponseBody
    @RequestMapping("findAlumno")
    public JsonResponse findAlumno(
            @RequestParam("nombre") String nombre,
            @RequestParam(name = "instanciaOficina", required = false) Long instanciaOficina,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<Alumno> alumnos = service.allAlumnoByOficina(nombre, instanciaOficina);
            for (Alumno alumno : alumnos) {
                data.add(JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
                    "id",
                    "codigo",
                    "persona.nombreCompleto",
                    "persona.apellidosNombres",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.*",
                    "carrera.*",
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
    public JsonResponse save(@RequestBody Resolucion resolucion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode data = new ArrayNode(JsonNodeFactory.instance);
            List<String> msg = new ArrayList();

            if (resolucion.isTipoReincorporacion()) {
                String token = service.saveReincorporacion(resolucion, ds.getUsuario(), ds);
                if (!token.isEmpty()) {
                    matriculableService.calcularPromedios(token, ds);
                    matriculableService.revisarCurriculaAlumnos(ds, token);
                    matriculableService.revisarMatriculables(ds, token);
                    matriculableService.generarAportes(ds, token);
                }

            } else if (resolucion.isTipoRetiroCiclo() || resolucion.isTipoAnulacionCiclo()) {
                String token = service.saveRetiroCiclo(resolucion, ds.getUsuario(), ds);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);

            } else if (resolucion.isTipoCambioNota()) {
                String token = service.saveCambioNota(resolucion, ds.getUsuario(), ds);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);

            } else if (Arrays.asList(TRAS_INT.name(), TRAS.name(), INTES.name(), ING_HIS.name()).contains(resolucion.getTipoResolucion().getCodigo())) {
                service.saveTramiteTraslado(resolucion, ds.getUsuario(), ds);
            } else if (resolucion.isTipoCursoDirigido()) {
                msg = service.saveCursoDirigido(resolucion, ds.getUsuario(), ds);
            } else if (resolucion.isTipoNotaBaja()) {
                String token = service.saveNotaMasBaja(resolucion, ds.getUsuario(), ds);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);
            } else if (resolucion.isTipoTramiteBachiller()) {
                service.saveResolucionTramiteBachiller(resolucion, ds);
            } else if (resolucion.isTipoTramiteTitulo()) {
                service.saveTramiteTitulo(resolucion, ds);
            } else if (resolucion.isTipoTramitePracticas()) {
                String token = service.saveResolucionTramitePracticas(resolucion, ds);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
            } else if (resolucion.isTipoReadmision()) {
                String token = service.saveReadmision(resolucion, ds.getUsuario(), ds);
                if (!token.isEmpty()) {
                    matriculableService.calcularPromedios(token, ds);
                    matriculableService.revisarCurriculaAlumnos(ds, token);
                    matriculableService.revisarMatriculables(ds, token);
                    matriculableService.generarAportes(ds, token);
                }
            } else if (resolucion.isTipoCambioPlanCurricular()) {
                String token = service.saveCambioPlanCurricular(resolucion, ds.getUsuario(), ds);
                if (!token.isEmpty()) {
                    matriculableService.calcularPromedios(token, ds);
                    matriculableService.revisarCurriculaAlumnos(ds, token);
                    matriculableService.revisarMatriculables(ds, token);
                    matriculableService.generarAportes(ds, token);
                }
            }

            response.setMessage("Se realizó el registro satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
            response.setData(msg);
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
    @RequestMapping("update")
    public JsonResponse update(@RequestBody Resolucion resolucion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<String> msg = new ArrayList();
            List<String> respuestas = new ArrayList();

            if (resolucion.isTipoReincorporacion()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);

            } else if (resolucion.isTipoRetiroCiclo() || resolucion.isTipoAnulacionCiclo()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);

            } else if (resolucion.isTipoCambioNota()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);

            } else if (Arrays.asList(TRAS_INT.name(), TRAS.name(), INTES.name(), ING_HIS.name()).contains(resolucion.getTipoResolucion().getCodigo())) {
                service.updateResolucion(resolucion, ds.getUsuario(), ds);
                if (TRAS_INT.name().equals(resolucion.getTipoResolucion().getCodigo())) {

                    service.generarNuevoPlan(resolucion, ds);
                }
            } else if (resolucion.isTipoCursoDirigido()) {
                msg = service.updateResolucion(resolucion, ds.getUsuario(), ds);
            } else if (resolucion.isTipoNotaBaja()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);
            } else if (resolucion.isTipoTramiteBachiller()) {
                service.updateResolucion(resolucion, ds.getUsuario(), ds);
            } else if (resolucion.isTipoTramiteTitulo()) {
                service.updateResolucion(resolucion, ds.getUsuario(), ds);
            } else if (resolucion.isTipoTramitePracticas()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
            } else if (resolucion.isTipoReadmision()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);
            } else if (resolucion.isTipoCambioPlanCurricular()) {
                respuestas = service.updateResolucion(resolucion, ds.getUsuario(), ds);
                String token = respuestas.get(0);
                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
                matriculableService.revisarMatriculables(ds, token);
                matriculableService.generarAportes(ds, token);
            }

            response.setMessage("Se realizó el registro satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
            response.setData(msg);
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
    public JsonResponse alumnos(@PathVariable("idResolucion") Long idResolucion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            Resolucion resolucionDB = service.findByResolucion(idResolucion, ds);
            this.findData(array, resolucionDB);

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

    private ObjectNode findDataResolucion(Resolucion resolucionDB) {

        List<RetiroCiclo> retiroCiclos = new ArrayList();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        ObjectNode objectNode = JsonHelper.createJson(resolucionDB, JsonNodeFactory.instance, new String[]{
            "*",
            "tipoResolucion.*",
            "oficina.*",
            "cicloAplica.*"
        });

        if (resolucionDB.getTipoResolucion().getCodigo().equals(BACHI.name())) {
            List<TramiteBachiller> bachillers = service.allTramiteBachiller(resolucionDB);
            for (TramiteBachiller bachiller : bachillers) {
                Tramite tramite = bachiller.getTramite();
                bachiller.setAlumno(tramite.getAlumno());
                ObjectNode node = JsonHelper.createJson(bachiller, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "tramite.*"
                });
                node.put("tipo", BACHI.name());
                array.add(node);
            }
            objectNode.set("tramiteBachiller", array);
        } else if (resolucionDB.getTipoResolucion().getCodigo().equals(TITUL.name())) {
            List<TramiteTitulo> titulo = service.allTramiteTitulo(resolucionDB);

            for (TramiteTitulo tit : titulo) {
                Tramite tramite = tit.getTramite();
                tit.setAlumno(tramite.getAlumno());
                ObjectNode node = JsonHelper.createJson(tit, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "tramite.*"
                });
                node.put("tipo", TITUL.name());
                array.add(node);
            }
            objectNode.set("tramiteTitulos", array);
        } else if (resolucionDB.getTipoResolucion().getCodigo().equals(PRACTICAS.name())) {
            List<PracticasPreProfesional> practicas = service.allPracticasPreProfesionales(resolucionDB);

            for (PracticasPreProfesional prac : practicas) {
                Tramite tramite = prac.getTramite();
                prac.setAlumno(tramite.getAlumno());
                ObjectNode node = JsonHelper.createJson(prac, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "tramite.*"
                });
                node.put("tipo", PRACTICAS.name());
                array.add(node);
            }
            objectNode.set("tramitePracticasPreProfesionales", array);
        } else if (resolucionDB.getTipoResolucion().getCodigo().equals(RCI.name())) {

            retiroCiclos = service.allRetiroCicloByResolucion(resolucionDB);
            for (RetiroCiclo retiroCiclo : retiroCiclos) {
                ObjectNode node = JsonHelper.createJson(retiroCiclo, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*",
                    "cicloAcademico.*"
                });
                node.put("tipo", RCI.name());
                array.add(node);
            }
            objectNode.set("retiroCiclo", array);
        }

        return objectNode;
    }

    private ArrayNode findData(ArrayNode array, Resolucion resolucion) {
        TipoResolucionEnum tipoResolucionEnum = resolucion.getTipoResolucion().getTipoEnum();

        ObjectNode objectNode;
        if (tipoResolucionEnum == REIC) {
            List<Reincorporacion> reincorporados = service.allReincorporacionByResolucion(resolucion);
            for (Reincorporacion reicorporacion : reincorporados) {
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

        } else if (tipoResolucionEnum == RCI) {
            List<RetiroCiclo> retiradosCiclos = service.allRetiroCicloByResolucion(resolucion);
            for (RetiroCiclo retiroCiclo : retiradosCiclos) {
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

        } else if (tipoResolucionEnum == CAM_NOTA) {
            List<CambioNota> cambiosNotas = service.allCambioNota(resolucion);
            for (CambioNota cambioNota : cambiosNotas) {
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

        } else if (tipoResolucionEnum == CURDIR) {
            List<CursoDirigido> cursosDirigidos = service.allCursodirigido(resolucion);
            for (CursoDirigido cursoDir : cursosDirigidos) {
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

        } else if (Arrays.asList(TRAS.name(), INTES.name(), ING_HIS.name(), TRAS_INT.name()).contains(tipoResolucionEnum.name())) {
            List<TramiteTraslado> trasladados = service.allTramiteTraslado(resolucion);
            for (TramiteTraslado traslado : trasladados) {
                objectNode = JsonHelper.createJson(traslado, JsonNodeFactory.instance, new String[]{
                    "*",
                    "carrera.*",
                    "carreraOrigen.*",
                    "tramite.cicloAcademico.id", "tramite.cicloAcademico.descripcion",
                    "tramite.alumno.*",
                    "tramite.alumno.persona.*",
                    "tramite.alumno.persona.tipoDocumento.*"
                });
                objectNode.put("tipo", tipoResolucionEnum.name());
                array.add(objectNode);
            }

        } else if (Arrays.asList(BACHI, TITUL, OBTE_GRADO).contains(tipoResolucionEnum)) {
            List<ObtencionGrado> graduados = service.allObtencionGrado(resolucion);
            for (ObtencionGrado gradux : graduados) {
                objectNode = JsonHelper.createJson(gradux, JsonNodeFactory.instance, new String[]{
                    "*",
                    "cicloAcademico.id",
                    "cicloAcademico.descripcion",
                    "gradoAcademico.nombre",
                    "alumno.codigo",
                    "alumno.persona.numeroDocIdentidad",
                    "alumno.persona.nombreCompleto",
                    "alumno.persona.tipoDocumento.simbolo"
                });
                objectNode.put("tipo", tipoResolucionEnum.name());
                array.add(objectNode);
            }
        } else if (tipoResolucionEnum == PRACTICAS) {
            List<PracticasPreProfesional> practicasPre = service.allPracticasPreProfesionales(resolucion);
            for (PracticasPreProfesional prac : practicasPre) {
                objectNode = JsonHelper.createJson(prac, JsonNodeFactory.instance, new String[]{
                    "*",
                    "curso.*",
                    "tramite.alumno.*",
                    "tramite.alumno.persona.*",
                    "tramite.alumno.persona.tipoDocumento.*", //                        "cicloAcademico.*"
                });
                objectNode.put("tipo", PRACTICAS.name());
                array.add(objectNode);
            }

        } else if (tipoResolucionEnum == READMISION) {
            List<Readmision> readmisiones = service.allReadmisionByResolucion(resolucion);
            for (Readmision readmisionn : readmisiones) {
                objectNode = JaneHelper.from(readmisionn)
                        .join("facultad")
                        .join("alumno")
                        .join("alumno.persona")
                        .join("alumno.persona.tipoDocumento")
                        .join("cicloReadmitido")
                        .json();
                objectNode.put("tipo", READMISION.name());
                array.add(objectNode);
            }
        } else if (tipoResolucionEnum == CAMBIO_PLAN_CURRICULAR) {
            List<CambioPlanCurricular> cambioPlanCurriculares = service.allCambioPlanCurricularByResolucion(resolucion);
            for (CambioPlanCurricular cambioPlanCurricular : cambioPlanCurriculares) {
                objectNode = JaneHelper.from(cambioPlanCurricular)
                        .join("facultad")
                        .join("alumno")
                        .join("alumno.persona")
                        .join("alumno.persona.tipoDocumento")
                        .join("cicloAcademico")
                        .json();
                objectNode.put("tipo", CAMBIO_PLAN_CURRICULAR.name());
                array.add(objectNode);
            }
        }

        return array;
    }

    @ResponseBody
    @RequestMapping("allCiclosRepetido/{idAlumno}")
    public JsonResponse allCiclosRepetido(@PathVariable(value = "idAlumno") Long idAlumno,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<AlumnoCicloCursoBean> alumnoCicloCursoBeans = service.allCiclosRepetido(idAlumno, ds);

            for (AlumnoCicloCursoBean alumnoCicloCursoBean : alumnoCicloCursoBeans) {
                array.add(JsonHelper.createJson(alumnoCicloCursoBean, JsonNodeFactory.instance, new String[]{
                    "alumno.id",
                    "curso.id",
                    "curso.nombre",
                    "curso.codigo",
                    "cicloAcademico.id",
                    "cicloAcademico.descripcion",
                    "nota",
                    "key",}));
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

    @ResponseBody
    @RequestMapping("allBachiller")
    public JsonResponse allBachiller(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteBachiller> tramitesBachillerBeans = service.allBachiller(ds);

            for (TramiteBachiller tramiteBachiller : tramitesBachillerBeans) {
                tramiteBachiller.setAlumno(tramiteBachiller.getTramite().getAlumno());
                tramiteBachiller.setSeleccionado(Boolean.FALSE);
                array.add(JsonHelper.createJson(tramiteBachiller, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.simbolo"
                }));
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

    @ResponseBody
    @RequestMapping("allTitulo")
    public JsonResponse allTitulo(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteTitulo> tramiteTitulos = service.allTitulos(ds);

            for (TramiteTitulo tramiteTitulo : tramiteTitulos) {
                tramiteTitulo.setAlumno(tramiteTitulo.getTramite().getAlumno());
                tramiteTitulo.setSeleccionado(Boolean.FALSE);
                array.add(JsonHelper.createJson(tramiteTitulo, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.simbolo"
                }));
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

    @ResponseBody
    @RequestMapping("allPracticas")
    public JsonResponse allPracticas(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<PracticasPreProfesional> practicasPreProfesionals = service.allPracticas(ds);

            for (PracticasPreProfesional ppp : practicasPreProfesionals) {
                ppp.setAlumno(ppp.getTramite().getAlumno());
                ppp.setSeleccionado(Boolean.FALSE);
                array.add(JsonHelper.createJson(ppp, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*"
                }));
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

    @ResponseBody
    @RequestMapping("allRetiroCiclo")
    public JsonResponse allRetiroCiclo(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<RetiroCiclo> retiroCiclos = service.allRetiroCiclo(ds);

            for (RetiroCiclo ppp : retiroCiclos) {
                ppp.setAlumno(ppp.getTramite().getAlumno());
                ppp.setSeleccionado(Boolean.FALSE);
                array.add(JsonHelper.createJson(ppp, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.carrera.*",
                    "alumno.carrera.facultad.*",
                    "alumno.persona.*",
                    "alumno.persona.tipoDocumento.*"
                }));
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

    @ResponseBody
    @RequestMapping("allReincorporacion")
    public JsonResponse allReincorporacion(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Reincorporacion> reincorporaciones = service.allReincorporacion();

            ArrayNode array = JaneHelper.from(reincorporaciones)
                    .join("cicloReincorporacion")
                    .join("alumno")
                    .join("alumno.carrera")
                    .join("alumno.carrera.facultad")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .array();

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

    @ResponseBody
    @RequestMapping("allReadmision")
    public JsonResponse allReadmision(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Readmision> readmisiones = service.allReadmision();

            ArrayNode array = JaneHelper.from(readmisiones)
                    .join("cicloReadmitido")
                    .join("alumno")
                    .join("alumno.carrera")
                    .join("alumno.carrera.facultad")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .array();

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

    @ResponseBody
    @RequestMapping("allCambioPlanCurricular")
    public JsonResponse allCambioPlanCurricular(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<CambioPlanCurricular> cambioPlanCurriculares = service.allCambioPlanCurricular();
            for (CambioPlanCurricular cambioPlanCurriculare : cambioPlanCurriculares) {
                ObjectUtil.printAttr(cambioPlanCurriculare);
            }

            ArrayNode array = JaneHelper.from(cambioPlanCurriculares)
                    .join("planCurricularOrigen")
                    .join("planCurricularOrigen.cicloInicioVigencia")
                    .join("planCurricularDestino")
                    .join("planCurricularDestino.cicloInicioVigencia")
                    .join("cicloAcademico")
                    .join("alumno")
                    .join("alumno.carrera")
                    .join("alumno.carrera.facultad")
                    .join("alumno.persona")
                    .join("alumno.persona.tipoDocumento")
                    .array();

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
