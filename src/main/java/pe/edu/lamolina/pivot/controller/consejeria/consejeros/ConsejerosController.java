package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.controller.academico.carrera.CarreraService;
import pe.edu.lamolina.pivot.controller.consejeria.aconsejadoscarrera.AconsejadosCarreraService;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.view.ConsejerosPorCarreraExcelView;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.view.ReporteAlumnosConsejeroExcelView;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.view.TutoradosConsejeroOtraCarreraExcelView;
import pe.edu.lamolina.pivot.controller.consejeria.consejeros.view.TutoradosPorCondicionExcelView;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/consejeros")
public class ConsejerosController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConsejerosService service;

    @Autowired
    CarreraService carreraService;

    @Autowired
    ReporteAlumnosConsejeroExcelView reporteAlumnosConsejeroExcelView;

    @Autowired
    ConsejerosPorCarreraExcelView consejerosPorCarreraExcelView;

    @Autowired
    AconsejadosCarreraService aconsejadoCarreraService;

    @Autowired
    TutoradosPorCondicionExcelView tutoradosPorCondicionExcelView;

    @Autowired
    TutoradosConsejeroOtraCarreraExcelView tutoradosConsejeroOtraCarreraExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        logger.debug("ciclo academico {}", ds.getCicloAcademico());
        logger.debug("persona id {}", ds.getPersona().getId());

        List<Carrera> carreras = service.allCarreraByPersonaCiclo(ds.getPersona(), ds.getCicloAcademico());
        logger.debug("carrera cantiad {}", carreras.size());

        model.addAttribute("ciclo", createCicloJson(ds.getCicloAcademico()).toString());
        model.addAttribute("carreras", createCarrerasJson(carreras).toString());

        return "consejeria/consejeros/consejeros";
    }

    @ResponseBody
    @RequestMapping("list/{carrera}")
    public DynatableResponse list(
            @PathVariable("carrera") Long idCarrera,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.revisarConsejeria(new Carrera(idCarrera), ds.getCicloAcademico(), false, ds);
            List<Consejero> consejeros = service.allByCarreraDynatable(new Carrera(idCarrera), ds.getCicloAcademico(), filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (Consejero consejero : consejeros) {
                ObjectNode node = JsonHelper.createJson(consejero, JsonNodeFactory.instance, true,
                        new String[]{
                            "id", "estado", "alumnosActivos", "alumnosInactivos",
                            "aconsejadosMat", "aconsejadosNmat",
                            "colaborador.persona.emailCompania",
                            "colaborador.persona.nombreCompleto",
                            "colaborador.persona.numeroDocIdentidad",
                            "colaborador.persona.tipoDocumento.simbolo",
                            "docente.departamentoAcademico.nombre",
                            "docente.codigo",
                            "docente.departamentoAcademico.id"
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
    @RequestMapping("revisionTotal")
    public JsonResponse revisionTotal(HttpSession session, HttpServletRequest request) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Carrera> carreras = service.allCarrerasPregrado();
            for (Carrera carrera : carreras) {
                service.revisarConsejeria(carrera, ds.getCicloAcademico(), true, ds);
            }
            json.setMessage("Búsqueda Exitosa");
            json.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("listDocente")
    public JsonResponse listDocente(
            @RequestParam String nombre,
            @RequestParam Long idFacultad, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {

            List<Docente> docentes = service.allDocenteByNombreFacultad(nombre, new Facultad(idFacultad));

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Docente docente : docentes) {

                ObjectNode node = JsonHelper.createJson(docente, JsonNodeFactory.instance, true, new String[]{
                    "id", "estado", "codigo",
                    "persona.id",
                    "persona.nombreCompleto",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.simbolo",
                    "departamentoAcademico.id",
                    "departamentoAcademico.nombre",
                    "departamentoAcademico.facultad.id"
                });
                array.add(node);
            }
            json.setData(array);
            json.setTotal(array.size());
            json.setMessage("Búsqueda Exitosa");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("saveConsejero")
    public JsonResponse saveConsejero(@RequestBody Docente docente, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();

        logger.debug("id_persona " + docente.getPersona().getId());
        logger.debug("id_dep " + docente.getDepartamentoAcademico().getId());
        logger.debug("carrera " + docente.getCarrera().getId());

        try {

            service.saveConsejeroByDocente(docente, ds.getCicloAcademico(), ds);
            json.setMessage("El Docente seleccionado ahora es Consejero.");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("cambiarEstado")
    public JsonResponse cambiarEstado(@RequestBody Consejero consejero, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.updateEstado(consejero, ds.getCicloAcademico(), ds);

            response.setMessage("El estado del consejero fue modificado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("resumenCarrera")
    public JsonResponse resumenCarrera(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ConsejeriaResumen resumen = service.getResumenByCarreraCiclo(new Carrera(idCarrera), ds.getCicloAcademico());
            ObjectNode consejeroJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});
            json.setData(consejeroJson);
            json.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("asignarAlumno")
    public JsonResponse asignarAlumno(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.asignarAlumnosAleatorio(new Carrera(idCarrera), ds.getCicloAcademico(), ds);
            json.setMessage("Los alumnos se asignaron de manera aleatoria satisfactoriamente");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("desasignarAlumno")
    public JsonResponse desasignarAlumno(@RequestParam("carrera") Long idCarrera, HttpSession session) {

        JsonResponse json = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.desasignarAlumnos(new Carrera(idCarrera), ds.getCicloAcademico(), ds);
            json.setMessage("Se retiraron los tutores a todos los alumnos satisfactoriamente");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{"id", "descripcion", "descripcion2"});
    }

    private ArrayNode createCarrerasJson(List<Carrera> carreras) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Carrera carrera : carreras) {
            ObjectNode node = JsonHelper.createJson(carrera, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre"
            });
            array.add(node);
        }
        return array;
    }

    @RequestMapping("reporteAlumnos/{carrera}")
    public ModelAndView reporteAlumnos(@PathVariable("carrera") Long idCarrera, @RequestParam("consejero") Long consejero, Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        if (consejero.intValue() != 0 && filter.getQueries() == null) {
            filter.setQueries(new HashMap());
            filter.getQueries().put("consjeroPrm", consejero);
        }
        Carrera carrera = new Carrera(idCarrera);
        List<Consejero> consejeros = service.allByCarreraDynatable(carrera, ds.getCicloAcademico(), filter);
        List<AlumnoConsejero> alumnosConsejero = service.allAlumnosConsejeros(consejeros, ds.getCicloAcademico(), EstadoEnum.ACT);

        List<Carrera> carreras = consejeros.stream().map(x -> x.getCarrera()).collect(Collectors.toList());
        List<MatriculaResumen> matriculados = service.allMatriculadosByCicloAndCarrera(ds.getCicloAcademico(), carreras);
        model.addAttribute("consejeros", consejeros);
        model.addAttribute("alumnosConsejero", alumnosConsejero);
        model.addAttribute("matriculados", matriculados);
        // model.addAttribute("alumnosConsejero", ds.getCicloAcademico());
        return new ModelAndView(reporteAlumnosConsejeroExcelView);
    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode jsonList = new ArrayNode(jFactory);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            List<Alumno> alumnos = service.allAlumnoByName(nombre, cicloAcademico);

            for (Alumno alumno : alumnos) {
                ObjectNode json = JsonHelper.createJson(alumno, jFactory, true,
                        new String[]{
                            "id",
                            "codigo",
                            "situacion",
                            "motivoMatriculable",
                            "persona.nombreCompleto",
                            "persona.rutaFotoDocumento",
                            "persona.rutaFotoPostulante",
                            "carrera.nombre",
                            "carrera.facultad.nombre",});
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("saveAlumnoConjero")
    public JsonResponse saveAlumnoConjero(@RequestBody Consejero consejero, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            ds.setFechaAccionAudit(new Date());
            service.saveAlumnosConsejero(consejero, ds);
            response.setMessage("Alumnos aconsejados agregados.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("consejerosPorEspecialidad")
    public ModelAndView consejerosPorEspecialidad(@RequestParam("carrera") Long idCarrera, Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        List<Consejero> consejeros = service.allByCarreraDynatable(new Carrera(idCarrera), ds.getCicloAcademico(), filter);
        model.addAttribute("consejeros", consejeros);
        return new ModelAndView(consejerosPorCarreraExcelView);
    }

    @RequestMapping("aconsejadosPorCondicion")
    public ModelAndView aconsejadosPorEstado(
            @RequestParam("carrera") Long idCarrera,
            @RequestParam("condicion") String condicion,
            Model model, HttpSession session) {
        //conConsejero sinConsejero inhabilitado
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);
        filter.setQueries(new HashMap());
        filter.getQueries().put("estado", condicion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        //  List<MatriculaResumen> matriculados = service.allMatriculadosByCicloAndCarrera(ds.getCicloAcademico(), carreras);
        List<AlumnoConsejero> alumnosTutores = aconsejadoCarreraService.allAconsejadoByDynatable(new Carrera(idCarrera), filter, ds.getCicloAcademico());

        model.addAttribute("condicion", condicion);
        model.addAttribute("alumnosConsejero", alumnosTutores);
        return new ModelAndView(tutoradosPorCondicionExcelView);
    }

    @RequestMapping("reporteTutoradosOtraEspecialidad")
    public ModelAndView reporteTutoradosOtraEspecialidad(
            @RequestParam("carrera") Long idCarrera,
            Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Carrera carrera = new Carrera(idCarrera);
        List<MatriculaResumen> matriculados = service.allMatriculadosByCicloAndCarrera(ds.getCicloAcademico(), Arrays.asList(carrera));
        List<AlumnoConsejero> allAlumnosOtraEspecialidad = service.allAlumnosOtraEspecialidad(carrera, ds.getCicloAcademico());
        model.addAttribute("alumnosConsejero", allAlumnosOtraEspecialidad);
        model.addAttribute("matriculados", matriculados);
        return new ModelAndView(tutoradosConsejeroOtraCarreraExcelView);
    }

}
