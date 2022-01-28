package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.ClonarConsejerosDTO;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.ReunionConsejerosEXCEL;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@Controller
@RequestMapping("consejeria/administracion")
public class AdministracionConsejeriaController {

    @Autowired
    AdministracionConsejeriaService service;

    @Autowired
    ReunionConsejerosEXCEL reunionConsejerosExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "consejeria/administracion/administracion";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ConsejeriaHistorial> consejeriaHistoriales = service.allConsejeriaHistorialByDynatable(filter, ds.getCicloAcademico());

        ArrayNode array = JaneHelper.from(consejeriaHistoriales).
                join("cicloAcademico", "id,descripcion").
                array();

        json.setData(array);
        json.setFiltered(filter.getFiltered());
        json.setTotal(filter.getTotal());
        return json;
    }

    @ResponseBody
    @RequestMapping("ciclo/all")
    public ArrayNode allCiclo() {
        List<CicloAcademico> cicloAcademicos = service.allCiclo();
        return JaneHelper.from(cicloAcademicos)
                .only("id,descripcion,codigo").array();
    }

    @ResponseBody
    @RequestMapping("clonar")
    public String clonar(@RequestBody @Valid ClonarConsejerosDTO clonarDTO, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.clonar(clonarDTO, ds);
        return GlobalMessages.UPDATED;
    }

    @ResponseBody
    @RequestMapping("eliminar/{idConsejeriaHistorial}")
    public String cloneliminarar(@PathVariable Long idConsejeriaHistorial, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.eliminar(idConsejeriaHistorial, ds);
        return GlobalMessages.DELETED;
    }

    @RequestMapping("agendaconsejero")
    public String agendaconsejero() {
        return "consejeria/administracion/agendaconsejeroadmin";
    }

    @RequestMapping("agendaconsejero/reporte")
    public ModelAndView reporte(@RequestBody FiltroReporteAgendaDTO filtroReporteAgendaDTO, Model model, HttpSession session) {
        
        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = service.allReunionAlumnoConsejeroReporte(filtroReporteAgendaDTO);
        model.addAttribute("reunionAlumnoConsejeros", reunionAlumnoConsejeros);
        return new ModelAndView(reunionConsejerosExcelView);
    }

    @ResponseBody
    @RequestMapping("agendaconsejero/all")
    public DynatableResponse allAgenda(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse response = new DynatableResponse();
        response.setTotal(0);

        List<AgendaConsejero> agendaConsejeros = service.agendaDynatable(filter);

        ArrayNode arrayNodeAgenda = new ArrayNode(JsonNodeFactory.instance);

        for (AgendaConsejero agendaConsejero : agendaConsejeros) {

            ObjectNode objectNode = JaneHelper.from(agendaConsejero)
                    .join("hora")
                    .join("consejero.colaborador.persona", "nombreCompleto")
                    .join("consejero.carrera", "nombre")
                    .join("consejero.carrera.facultad", "nombre")
                    .json();

            objectNode.set("reunionAlumnoConsejeros",
                    JaneHelper.from(agendaConsejero.getReunionAlumnoConsejeros())
                            .join("alumnoConsejero")
                            .join("alumnoConsejero.alumno","id,codigo")
                            .join("alumnoConsejero.alumno.carrera","nombre")
                            .join("alumnoConsejero.alumno.carrera.facultad","nombre")
                            .join("alumnoConsejero.alumno.persona","id,nombreCompleto,apellidosNombres,emailCompania")
                            .join("alumnoConsejero.hora")
                            .array());

            arrayNodeAgenda.add(objectNode);

        }

        response.setData(arrayNodeAgenda);
        response.setFiltered(filter.getFiltered());
        response.setTotal(filter.getTotal());
        return response;
    }

    @ResponseBody
    @RequestMapping("allCarrera")
    public ArrayNode allCarrera(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Carrera> carreras = service.buscarCarrera(nombre);
        return JaneHelper.from(carreras)
                .only("id,nombre")
                .join("modalidadEstudio", "nombre").array();
    }

    @ResponseBody
    @RequestMapping("allConsejero")
    public ArrayNode allConsejero(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Consejero> consejeros = service.buscarConsejero(nombre);
        return JaneHelper.from(consejeros)
                .only("id")
                .join("colaborador", "id")
                .join("carrera", "id,nombre")
                .join("colaborador.persona", "nombreCompleto")
                .array();
    }

    @ResponseBody
    @RequestMapping("allAlumno")
    public ArrayNode allAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        List<Alumno> alumnos = service.buscarAlumno(nombre);
        
        return JaneHelper.from(alumnos)
                .only("id,codigo")
                .join("persona", "id,apellidosNombres")
                .join("carrera", "id,nombre")
                .array();
    }

}
