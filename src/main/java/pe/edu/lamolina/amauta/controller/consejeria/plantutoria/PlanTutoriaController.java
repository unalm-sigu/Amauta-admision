package pe.edu.lamolina.amauta.controller.consejeria.plantutoria;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
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
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.PlanTutorial;
import pe.edu.lamolina.model.tutoria.TipoCualidadAlumno;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("consejeria/aconsejadostutor")
public class PlanTutoriaController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final PlanTutoriaService service;
    private final VerificadorService verificadorService;

    @RequestMapping(value = "{idAlumno}/planificacion", method = RequestMethod.GET)
    public String index(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Alumno alumno = service.findAlumno(new Alumno(idAlumno));
        AlumnoConsejero alumnoConsejero = service.findAlumnoConsejero(alumno, ciclo);

        model.addAttribute("alumnoJson", this.createAlumnoJson(alumno));
        model.addAttribute("consejeroJson", this.createConsejeroJson(alumnoConsejero.getConsejero()));
        model.addAttribute("alumnoConsejeroJson", this.createAlumnoConsejeroJson(alumnoConsejero));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("tienePermiso", service.tienePermiso(alumno, ciclo, ds));
        model.addAttribute("esConsejero", service.verificarConsejero(alumno, ciclo, ds));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/consejeria/aconsejadostutor"));

        return "consejeria/plantutoria/planTutoria";
    }

    @ResponseBody
    @RequestMapping("allTiposCualidades")
    public JsonResponse allTiposCualidades(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<TipoCualidadAlumno> tiposCualidades = service.allTiposCualidades();

        JsonResponse json = new JsonResponse();
        json.setData(this.createTiposCualidadesJson(tiposCualidades));
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("allCualidadesAlumno")
    public JsonResponse allCualidadesAlumno(@RequestBody Alumno alumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<AlumnoCualidad> cualidadesAlumno = service.allCualidadesAlumno(alumno, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setData(this.createCualidadesJson(cualidadesAlumno));
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("allPlanesTutoria")
    public JsonResponse allPlanesTutoria(@RequestBody Alumno alumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<PlanTutorial> planes = service.allPlanesTutoria(alumno, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setData(this.createPlanesJson(planes));
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/saveCaracteristicas")
    public JsonResponse saveCaracteristicas(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestBody List<AlumnoCualidad> cualidades, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveCaracteristicas(cualidades, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/savePlan")
    public JsonResponse savePlan(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestBody List<PlanTutorial> planes, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.savePlanTutorial(planes, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/deletePlan")
    public JsonResponse deletePlan(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestBody PlanTutorial plan, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.deletePlanTutorial(plan, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/tienePlan")
    public JsonResponse tienePlan(@PathVariable("idAlumno") Long idAlumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        boolean tieneCaracteristicas = service.tieneCaracteristicas(new Alumno(idAlumno), ds.getCicloAcademico(), ds);
        boolean tieneMapaEmpatia = service.tieneMapaEmpatia(new Alumno(idAlumno), ds.getCicloAcademico(), ds);
        boolean tienePlan = service.tienePlanTutorial(new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("tienePlan", tienePlan);
        node.put("tieneCaracteristicas", tieneCaracteristicas);
        node.put("tieneMapaEmpatia", tieneMapaEmpatia);

        JsonResponse json = new JsonResponse();
        json.setData(node);
        json.setSuccess(true);

        return json;
    }

    private ArrayNode createPlanesJson(List<PlanTutorial> planes) {
        return JaneHelper
                .from(planes)
                .join("alumno", "id")
                .join("cicloAcademico", "id")
                .array();
    }

    private ArrayNode createCualidadesJson(List<AlumnoCualidad> cualidades) {
        return JaneHelper
                .from(cualidades)
                .join("tipoCualidadAlumno")
                .array();
    }

    private ArrayNode createTiposCualidadesJson(List<TipoCualidadAlumno> tipos) {
        return JaneHelper
                .from(tipos)
                .array();
    }

    private ObjectNode createConsejeroJson(Consejero consejero) {
        return JaneHelper
                .from(consejero)
                .only("id,estado,fechaInicio,fechaFin")
                .join("carrera", "codigo,nombre")
                .join("colaborador", "id")
                .join("colaborador.persona", "apellidosNombres,numeroDocIdentidad")
                .join("colaborador.persona.tipoDocumento", "simbolo")
                .json();
    }

    private ObjectNode createAlumnoJson(Alumno alumno) {
        return JaneHelper
                .from(alumno)
                .only("id,codigo")
                .join("persona", "apellidosNombres,numeroDocIdentidad,sexo,email,emailCompania,telefono,celular,tipoFoto,rutaFoto")
                .join("persona.tipoDocumento", "simbolo")
                .join("carrera", "codigo,nombre")
                .join("carrera.facultad", "codigo,nombre")
                .join("modalidadEstudio", "codigo,nombre")
                .json();
    }

    private ObjectNode createAlumnoConsejeroJson(AlumnoConsejero alumnoConsejero) {
        return JaneHelper
                .from(alumnoConsejero)
                .only("id,estado,beneficioUtlimoCiclo")
                .join("cicloAcademico", "id,descripcion")
                .join("alumno", "id,codigo")
                .join("consejero", "id")
                .json();
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

}
