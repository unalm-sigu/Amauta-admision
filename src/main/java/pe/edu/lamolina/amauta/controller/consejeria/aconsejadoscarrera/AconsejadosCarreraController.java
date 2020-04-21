package pe.edu.lamolina.amauta.controller.consejeria.aconsejadoscarrera;

import pe.edu.lamolina.amauta.controller.consejeria.consejeros.ConsejerosService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.amauta.controller.academico.carrera.CarreraService;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("consejeria/aconsejadoscarrera")
public class AconsejadosCarreraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AconsejadosCarreraService service;
    @Autowired
    ConsejerosService consejeroService;
    @Autowired
    CarreraService carreraService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Carrera> carreras = consejeroService.allCarreraByPersonaCiclo(ds.getPersona(), ds.getCicloAcademico());

        model.addAttribute("ciclo", JsonHelper.createJson(ds.getCicloAcademico(), JsonNodeFactory.instance, new String[]{"*"}));
        model.addAttribute("carreras", createCarrerasJson(carreras).toString());

        return "consejeria/aconsejadoscarrera/aconsejadosCarrera";
    }

    @ResponseBody
    @RequestMapping("list/{carrera}")
    public DynatableResponse list(
            @PathVariable("carrera") Long idCarrera,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            consejeroService.revisarConsejeria(new Carrera(idCarrera), ds.getCicloAcademico(), false, ds);
            List<AlumnoConsejero> alumnosTutores = service.allAconsejadoByDynatable(new Carrera(idCarrera), filter, ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (AlumnoConsejero alumnoTutor : alumnosTutores) {
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
                            "consejero.colaborador.persona.tipoDocumento.simbolo"
                        });

                array.add(node);
            }

            json.setData(array);
            json.setFiltered(filter.getFiltered());
            json.setTotal(filter.getTotal());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("listConsejero")
    public JsonResponse listConsejero(
            @RequestParam String nombre,
            @RequestParam Long idCarrera, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        try {

            List<Consejero> consejeros = consejeroService.allByCarrera(nombre, new Carrera(idCarrera));

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Consejero consejero : consejeros) {

                ObjectNode node = JsonHelper.createJson(consejero, JsonNodeFactory.instance, true, new String[]{
                    "*",
                    "colaborador.persona.*"
                });
                array.add(node);
            }
            json.setData(array);
            json.setTotal(array.size());
            json.setMessage("Búsqueda Exitosa");

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
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
    @RequestMapping("update")
    public JsonResponse saveConsejero(@RequestBody AlumnoConsejero alumnoConsejeroForm, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();

        try {

            service.updateAlumnoConsejero(alumnoConsejeroForm, ds);
            json.setMessage("El Actualizó el registro satisfactoriamente.");
            json.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
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
}
