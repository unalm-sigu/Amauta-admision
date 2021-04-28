package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.view.ReporteAconsejadosTutorExcelView;
import pe.edu.lamolina.amauta.controller.matricula.tutorsolicitud.TutorSolicitudService;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;

@Controller
@RequestMapping("consejeria/aconsejadostutor")
public class AconsejadosTutorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AconsejadosTutorService service;

    @Autowired
    TutorSolicitudService tutorSolicitudservice;

    @Autowired
    ReporteAconsejadosTutorExcelView reporteAlumnosConsejeroExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("persona", ds.getPersona());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        return "consejeria/aconsejadostutor/aconsejadosTutor";
    }

    @RequestMapping(value = "viewCoordinador/{idPersona}/{idCarrera}", method = RequestMethod.GET)
    public String aconsejadosTutor(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Persona persona = service.findPersona(idPersona);
        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("persona", JsonHelper.createJson(persona, JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("carrera", JsonHelper.createJson(new Carrera(idCarrera), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());

        return "consejeria/viewCoordinador/viewCoordinador";
    }

    @ResponseBody
    @RequestMapping("list/{idPersona}/{idCarrera}")
    public DynatableResponse list(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            Persona persona = service.findPersona(idPersona);
            List<AlumnoConsejero> alumnosTutor = service.allByDynatableByCarrera(filter, ds.getCicloAcademico(), persona, new Carrera(idCarrera));
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

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("countData")
    public JsonResponse countData(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersona(ds.getPersona(), ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            json.setData(JsonHelper.createJson(aconsejadoEstadoBean, JsonNodeFactory.instance, new String[]{"*"}));
            json.setMessage("Búsqueda Exitosa");

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("countData/{idPersona}/{idCarrera}")
    public JsonResponse countData(@PathVariable("idPersona") Long idPersona, @PathVariable("idCarrera") Long idCarrera, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            Persona person = service.findPersona(idPersona);
            AconsejadoEstadoBean aconsejadoEstadoBean = service.allByPersonaCarrera(person, ds.getCicloAcademico(), new Carrera(idCarrera));
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            json.setData(JsonHelper.createJson(aconsejadoEstadoBean, JsonNodeFactory.instance, new String[]{"*"}));
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
        List<AlumnoConsejero> alumnosTutor = service.allByDynatableByCarrera(filter, ds.getCicloAcademico(), persona, new Carrera(idCarrera));
        Consejero consejero = alumnosTutor.stream().map(x -> x.getConsejero()).findAny().orElse(null);
        model.addAttribute("alumnosTutor", alumnosTutor);
        model.addAttribute("consejero", consejero);
        model.addAttribute("dataSession", ds.getCicloAcademico());
        return new ModelAndView(reporteAlumnosConsejeroExcelView);
    }

}
