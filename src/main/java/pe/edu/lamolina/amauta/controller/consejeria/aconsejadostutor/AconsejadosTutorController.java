package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view.ReporteAconsejadosTutorExcelView;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view.ResumenEncuestaTutoria;
import pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud.TutorSolicitudService;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import static pe.edu.lamolina.model.enums.consejeria.TipoCualidadAlumnoEnum.CARACTERISTICA;
import static pe.edu.lamolina.model.enums.consejeria.TipoCualidadAlumnoEnum.MAPA_EMPATIA;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("consejeria/aconsejadostutor")
public class AconsejadosTutorController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final AconsejadosTutorService service;
    private final TutorSolicitudService tutorSolicitudservice;
    private final ReporteAconsejadosTutorExcelView reporteAlumnosConsejeroExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Consejero> consejeros = service.allConsejeroCarrera(ds.getPersona(), ds.getCicloAcademico());
        Consejero consejero = consejeros.isEmpty() ? new Consejero() : consejeros.get(0);
        InformeFinalTutoria informe = service.findInforme(consejero, ds.getCicloAcademico(), ds);

        model.addAttribute("consejeroJson", this.createConsejeroJson(consejero));
        model.addAttribute("personaJson", this.createPersonaJson(ds.getPersona()));
        model.addAttribute("departamentoJson", this.createDepartamentoJson(ds.getDepartamentoAcademico()));
        model.addAttribute("cicloJson", this.createCicloJson(ds.getCicloAcademico()));
        model.addAttribute("informeJson", this.createInformeJson(informe));
        model.addAttribute("rutaModulo", rutaModulo);

        return "consejeria/aconsejadostutor/aconsejadosTutor";
    }

    @RequestMapping(value = "viewCoordinador/{idPersona}/{idCarrera}", method = RequestMethod.GET)
    public String aconsejadosTutor(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera,
            @PathParam("origen") String origen,
            Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Persona persona = service.findPersona(idPersona);
        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("persona", JsonHelper.createJson(persona, JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("carrera", JsonHelper.createJson(new Carrera(idCarrera), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        model.addAttribute("origen", getOrigen(origen));
        model.addAttribute("rutaModulo", rutaModulo);

        return "consejeria/viewCoordinador/viewCoordinador";
    }

    @ResponseBody
    @RequestMapping("list/{idPersona}/{idCarrera}")
    public DynatableResponse list(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        json.setTotal(0);

        Persona persona = service.findPersona(idPersona);
        List<AlumnoConsejero> alumnosTutor = service.allByDynatableByCarrera(filter, ds.getCicloAcademico(), persona, new Carrera(idCarrera), ds);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoConsejero alumnoTutor : alumnosTutor) {
            ObjectNode node = JsonHelper.createJson(alumnoTutor, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "alumno.id",
                        "alumno.codigo",
                        "alumno.creditosCursados",
                        "alumno.creditosAprobados",
                        "alumno.promedioAcumulado",
                        "alumno.cicloIngreso.descripcion",
                        "alumno.situacionAcademica.codigo",
                        "alumno.situacionAcademica.nombre",
                        "alumno.persona.emailCompania",
                        "alumno.persona.tipoFoto",
                        "alumno.persona.sexo",
                        "alumno.persona.rutaFoto",
                        "alumno.persona.apellidosNombres",
                        "alumno.persona.numeroDocIdentidad",
                        "alumno.persona.tipoDocumento.simbolo",
                        "alumno.carrera.nombre",
                        "alumno.carrera.facultad.nombre",
                        "consejero.*",
                        "consejero.colaborador.codigo",
                        "consejero.colaborador.persona.emailCompania",
                        "consejero.colaborador.persona.numeroDocIdentidad",
                        "consejero.colaborador.persona.apellidosNombres",
                        "consejero.colaborador.persona.tipoDocumento.simbolo",
                        "cicloAcademico.descripcion"
                    });

            array.add(node);
        }

        json.setFiltered(filter.getFiltered());
        json.setData(array);
        json.setTotal(filter.getTotal());

        return json;
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<AlumnoConsejero> alumnosConsejeros = service.allByCicloPersona(ds.getCicloAcademico(), ds.getPersona());

        List<AlumnoConsejero> alumnosTutor = service.allByDynatableByCarrera(filter, ds.getCicloAcademico(), ds.getPersona(), alumnosConsejeros.get(0).getConsejero().getCarrera(), ds);

        List<Alumno> alumnos = alumnosTutor.stream().map(tutor -> tutor.getAlumno()).collect(Collectors.toList());
        Map<Long, List<PlanTutorial>> mapPlanes = service.allPlanes(alumnos, ds.getCicloAcademico());
        Map<Long, List<AlumnoCualidad>> mapCualidades = service.allCualidades(alumnos, ds.getCicloAcademico());
        Map<Long, CitaConsejeroAlumno> mapCitas = service.allCitas(alumnos, ds.getCicloAcademico());

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoConsejero tutorado : alumnosTutor) {
            ObjectNode node = JaneHelper
                    .from(tutorado)
                    .join("alumno", "id,codigo,creditosCursados,creditosAprobados,promedioAcumulado")
                    .join("alumno.cicloIngreso", "descripcion")
                    .join("alumno.postulantePregrado.modalidadIngreso", "nombre")
                    .join("alumno.postulantePregrado.cicloPostula.cicloAcademico", "descripcion")
                    .join("alumno.situacionAcademica", "codigo,nombre,descripcion,nivelRiesgo")
                    .join("alumno.persona", "apellidosNombres,numeroDocIdentidad,emailCompania,sexo,tipoFoto,rutaFoto")
                    .join("alumno.persona.tipoDocumento", "simbolo")
                    .join("alumno.carrera", "codigo,nombre,tipo,tipoEnum")
                    .join("alumno.carrera.facultad", "nombre")
                    .join("alumno.modalidadEstudio", "codigo,nombre")
                    .join("consejero", "id")
                    .join("consejero.colaborador", "codigo")
                    .join("cicloAcademico", "id,descripcion")
                    .json();

            List<PlanTutorial> planes = mapPlanes.get(tutorado.getAlumno().getId());
            List<AlumnoCualidad> cualidades = mapCualidades.get(tutorado.getAlumno().getId());
            List<AlumnoCualidad> caracter = this.allCaracteristicas(cualidades);
            List<AlumnoCualidad> empatia = this.allMapaEmpatia(cualidades);
            CitaConsejeroAlumno cita = mapCitas.get(tutorado.getAlumno().getId());

            node.put("tienePlanes", !planes.isEmpty());
            node.put("tieneCaracterizacion", !caracter.isEmpty());
            node.put("tieneMapaEmpatia", !empatia.isEmpty());
            node.set("ultimoMensaje", this.createCitaJson(cita));

            array.add(node);
        }

        json.setFiltered(filter.getFiltered());
        json.setData(array);
        json.setTotal(filter.getTotal());

        return json;
    }

    private ObjectNode createCitaJson(CitaConsejeroAlumno cita) {
        return JaneHelper
                .from(cita)
                .only("id,estado,fecha,hora,estadoEnum")
                .json();
    }

    private List<AlumnoCualidad> allCaracteristicas(List<AlumnoCualidad> cualidades) {
        return cualidades.stream()
                .filter(cualidad -> cualidad.getTipoCualidadAlumno().getTipoCualidadEnum() == CARACTERISTICA)
                .collect(Collectors.toList());
    }

    private List<AlumnoCualidad> allMapaEmpatia(List<AlumnoCualidad> cualidades) {
        return cualidades.stream()
                .filter(cualidad -> cualidad.getTipoCualidadAlumno().getTipoCualidadEnum() == MAPA_EMPATIA)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping("countData")
    public JsonResponse countData(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersona(ds.getPersona(), ds.getCicloAcademico());

        JsonResponse json = new JsonResponse();
        json.setData(JaneHelper.from(aconsejadoEstadoBean).json());
        json.setSuccess(Boolean.TRUE);
        json.setMessage("Búsqueda Exitosa");

        return json;
    }

    @ResponseBody
    @RequestMapping("countData/{idPersona}/{idCarrera}")
    public JsonResponse countData(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            Persona person = service.findPersona(idPersona);
            AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersonaCarrera(person, ds.getCicloAcademico(), new Carrera(idCarrera), ds);
            ObjectNode node = JsonHelper.createJson(aconsejadoEstadoBean, JsonNodeFactory.instance, new String[]{"*"});
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoConsejero alumnosConsejero : aconsejadoEstadoBean.getAlumnosConsejeros()) {
                array.add(JsonHelper.createJson(alumnosConsejero, JsonNodeFactory.instance, new String[]{
                    "alumno.id",
                    "alumno.situacionAcademica.codigo",
                    "alumno.situacionAcademica.nombre"}));
            }
            node.set("alumnosConsejeros", array);
            json.setData(node);
            json.setMessage("Búsqueda Exitosa");

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("matriculaAutorizacion")
    public JsonResponse matriculaAutorizacion(@RequestBody MatriculaResumen matriculaResumen, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {
            service.matriculaAutorizacion(matriculaResumen, ds);
            json.setMessage("La autorización de matricula fue modificada satisfactoriamente");
            json.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("solicitudBeneficio")
    public JsonResponse matriculaAutorizacion(@RequestBody AlumnoConsejero alumnoConsejero, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        json.setSuccess(false);
        try {
            tutorSolicitudservice.solicitudBeneficio(alumnoConsejero, ds);
            json.setMessage("Se envio la solicitud de beneficio de último ciclo");
            json.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping("reporteAlumnosAconsejados")
    public ModelAndView reporteAlumnos(Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<AlumnoConsejero> alumnosTutor = service.allByDynatable(filter, ds.getCicloAcademico(), ds.getPersona());
        Consejero consejero = alumnosTutor.stream().map(x -> x.getConsejero()).findAny().orElse(null);
        model.addAttribute("alumnosTutor", alumnosTutor);
        model.addAttribute("consejero", consejero);
        model.addAttribute("dataSession", ds.getCicloAcademico());
        return new ModelAndView(reporteAlumnosConsejeroExcelView);
    }

    @RequestMapping("reporteAlumnosAconsejados/{idPersona}")
    public ModelAndView reporteAlumnos(@PathVariable("idPersona") Long idPersona, Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Persona persona = service.findPersona(idPersona);
        List<AlumnoConsejero> alumnosTutor = service.allByDynatable(filter, ds.getCicloAcademico(), persona);
        Consejero consejero = alumnosTutor.stream().map(x -> x.getConsejero()).findAny().orElse(null);
        model.addAttribute("alumnosTutor", alumnosTutor);
        model.addAttribute("consejero", consejero);
        model.addAttribute("dataSession", ds.getCicloAcademico());
        return new ModelAndView(reporteAlumnosConsejeroExcelView);
    }

    @RequestMapping("reporteAlumnosAconsejados/{idPersona}/{idCarrera}")
    public ModelAndView reporteAlumnos(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Persona persona = service.findPersona(idPersona);
        List<AlumnoConsejero> alumnosTutor = service.allByDynatableByCarreraReporte(filter, ds.getCicloAcademico(), persona, new Carrera(idCarrera));
        Consejero consejero = alumnosTutor.stream().map(x -> x.getConsejero()).findAny().orElse(null);
        model.addAttribute("alumnosTutor", alumnosTutor);
        model.addAttribute("consejero", consejero);
        model.addAttribute("dataSession", ds.getCicloAcademico());
        return new ModelAndView(reporteAlumnosConsejeroExcelView);
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/alumno";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    @ResponseBody
    @RequestMapping("eliminar/{idAlumnoConsejero}")
    public String eliminar(@PathVariable("idAlumnoConsejero") Long idAlumnoConsejero, Model model, HttpSession session) {
        service.eliminarAlumnoConsejero(idAlumnoConsejero);
        return GlobalMessages.DELETED;
    }

    @ResponseBody
    @RequestMapping("quitar/tutor/{idAlumnoConsejero}")
    public String quitarTutor(@PathVariable("idAlumnoConsejero") Long idAlumnoConsejero, Model model, HttpSession session) {
        service.quitarTutor(idAlumnoConsejero);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("allDataEncu")
    public JsonResponse allDataEncu(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<OpcionPregunta> opciones = new ArrayList();
        ExamenVirtual encuesta = new ExamenVirtual();

        Consejero consejero = service.findConsejero(ds.getPersona(), ds.getCicloAcademico());
        List<PreguntaExamen> preguntas = service.allPreguntasEncuesta(ds.getCicloAcademico());
        List<ResumenEncuestaTutoria> resumenes = service.allDataEncuesta(consejero, preguntas, ds.getCicloAcademico(), ds);

        if (!preguntas.isEmpty()) {
            opciones = preguntas.get(0).getOpcionesPregunta();
            encuesta = preguntas.get(0).getExamenVirtual();
        }

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("preguntas", this.createPreguntasJson(preguntas));
        node.set("opciones", this.createOpcionesJson(opciones));
        node.set("encuesta", this.createEncuestaJson(encuesta));
        node.set("respuestas", this.createRespuestaJson(resumenes));

        JsonResponse json = new JsonResponse();
        json.setData(node);
        json.setSuccess(true);

        return json;
    }

    private ArrayNode createRespuestaJson(List<ResumenEncuestaTutoria> resumenes) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (ResumenEncuestaTutoria resumen : resumenes) {
            ObjectNode node = JaneHelper
                    .from(resumen)
                    .join("pregunta", "id,numero,texto")
                    .json();

            ArrayNode rptas = new ArrayNode(JsonNodeFactory.instance);
            Map<String, BigDecimal> puntajes = resumen.getPuntajes();
            for (Map.Entry<String, BigDecimal> puntaje : puntajes.entrySet()) {
                ObjectNode nodeRpta = new ObjectNode(JsonNodeFactory.instance);
                nodeRpta.put(puntaje.getKey(), puntaje.getValue());
                rptas.add(nodeRpta);
            }

            node.set("puntajes", rptas);
            array.add(node);
        }

        return array;
    }

    private ObjectNode createEncuestaJson(ExamenVirtual encuesta) {
        return JaneHelper
                .from(encuesta)
                .only("id,nombre,codigo,estado")
                .json();
    }

    private ArrayNode createPreguntasJson(List<PreguntaExamen> preguntas) {
        return JaneHelper
                .from(preguntas)
                .only("id,estado,tipo,numero,texto")
                .join("examenVirtual", "codigo,nombre")
                .array();
    }

    private ArrayNode createOpcionesJson(List<OpcionPregunta> opciones) {
        return JaneHelper
                .from(opciones)
                .only("id,numero,letra,contenido")
                .array();
    }

    private ObjectNode createConsejeroJson(Consejero consejero) {
        return JaneHelper
                .from(consejero)
                .only("id,estado,fechaInicio,fechaFin")
                .join("carrera", "id,codigo,nombre")
                .join("colaborador", "id")
                .join("colaborador.persona", "apellidosNombres,numeroDocIdentidad")
                .join("colaborador.persona.tipoDocumento", "simbolo")
                .json();
    }

    private ObjectNode createPersonaJson(Persona persona) {
        return JaneHelper
                .from(persona)
                .only("id,nombreCompleto")
                .json();
    }

    private ObjectNode createDepartamentoJson(DepartamentoAcademico departamento) {
        if (departamento == null) {
            departamento = new DepartamentoAcademico();
        }
        return JaneHelper
                .from(departamento)
                .only("id,codigo,nombre")
                .json();
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ObjectNode createInformeJson(InformeFinalTutoria informe) {
        return JaneHelper
                .from(informe)
                .only("id,estado,comentarioInforme")
                .json();
    }

    private ArrayNode createCarrerasJson(List<Carrera> carreras) {
        return JaneHelper
                .from(carreras)
                .only("id,codigo,nombre")
                .array();
    }

    private ObjectNode createCarrerasJson(Carrera carrera) {
        return JaneHelper
                .from(carrera)
                .only("id,codigo,nombre")
                .json();
    }

}
