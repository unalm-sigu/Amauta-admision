package pe.edu.lamolina.amauta.controller.consejeria.aconsejadoscarrera;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor.AconsejadosTutorController;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@Controller
@RequestMapping("consejeria/aconsejadoscarrera")
public class AconsejadosCarreraController {

    @Autowired
    AconsejadosCarreraService service;
    @Autowired
    AconsejadosTutorController aconsejadosTutorController;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<Carrera> carreras = service.allCarreraByPersonaCiclo(ds.getPersona(), ds.getCicloAcademico());

        model.addAttribute("ciclo", JaneHelper.from(ds.getCicloAcademico()).json().toString());

        model.addAttribute("restriccionCape", service.isRolCape(ds));

        model.addAttribute("carreras", JaneHelper.from(carreras)
                .only("id,nombre,codigo")
                .join("facultad", "id,codigo,nombre")
                .array()
                .toString());

        model.addAttribute("esInformaticoOERA", service.esInformaticoOERA(ds));
        model.addAttribute("esAdministradorTutoria", service.esAdministradorTutoria(ds));
        
        model.addAttribute("RUTA_MODULO", getClass().getAnnotation(RequestMapping.class).value()[0]);
        
        model.addAttribute("rutaModuloTutor", aconsejadosTutorController.rutaModulo);

        return "consejeria/aconsejadoscarrera/aconsejadosCarrera";
    }

    @ResponseBody
    @RequestMapping("list/{carrera}")
    public DynatableResponse list(
            @PathVariable("carrera") Long idCarrera,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

//        service.revisarConsejeria(new Carrera(idCarrera), ds.getCicloAcademico(), false, ds);
        log.debug("idCarrera {}", idCarrera);
        log.debug("ds.getCicloAcademico() {}", ds.getCicloAcademico().getId());
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
                        "consejero.colaborador.codigo",
                        "consejero.colaborador.persona.emailCompania",
                        "consejero.colaborador.persona.numeroDocIdentidad",
                        "consejero.colaborador.persona.apellidosNombres",
                        "consejero.colaborador.persona.tipoDocumento.simbolo",
                        "cicloAcademico.descripcion"
                    });

            array.add(node);
        }

        json.setData(array);
        json.setFiltered(filter.getFiltered());
        json.setTotal(filter.getTotal());

        return json;
    }

    @ResponseBody
    @RequestMapping("listConsejero")
    public ArrayNode listConsejero(
            @RequestParam String nombre,
            @RequestParam Long idCarrera, HttpSession session) {

        List<Consejero> consejeros = service.allByCarrera(nombre, new Carrera(idCarrera));
        return JaneHelper.from(consejeros)
                .join("colaborador.persona")
                .array();
    }

    @ResponseBody
    @RequestMapping("resumenCarrera/{idCarrera}")
    public ObjectNode resumenCarrera(@PathVariable Long idCarrera, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        ConsejeriaResumen resumen = service.getResumenByCarreraCiclo(new Carrera(idCarrera), ds.getCicloAcademico());
        return JaneHelper.from(resumen).json();

    }

    @ResponseBody
    @RequestMapping("update")
    public String saveConsejero(@RequestBody AlumnoConsejero alumnoConsejeroForm, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.updateAlumnoConsejero(alumnoConsejeroForm, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("solicitudBeneficio")
    public String matriculaAutorizacion(@RequestBody AlumnoConsejero alumnoConsejero, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.solicitudBeneficio(alumnoConsejero, ds);
        return "Se envio la solicitud de beneficio de último ciclo";
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

}
