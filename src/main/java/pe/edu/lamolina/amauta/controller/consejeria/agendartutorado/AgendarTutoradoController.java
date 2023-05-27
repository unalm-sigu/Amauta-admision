package pe.edu.lamolina.amauta.controller.consejeria.agendartutorado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("consejeria/aconsejadostutor")
public class AgendarTutoradoController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final AgendarTutoradoService service;
    private final PlanTutoriaService planTutoriaService;
    private final VerificadorService verificadorService;

    @RequestMapping("{idAlumno}/agendarTutorado")
    public String index(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Alumno alumno = planTutoriaService.findAlumno(new Alumno(idAlumno));
        AlumnoConsejero alumnoConsejero = planTutoriaService.findAlumnoConsejero(alumno, ciclo);

        model.addAttribute("alumnoJson", this.createAlumnoJson(alumno));
        model.addAttribute("consejeroJson", this.createConsejeroJson(alumnoConsejero.getConsejero()));
        model.addAttribute("alumnoConsejeroJson", this.createAlumnoConsejeroJson(alumnoConsejero));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("tienePermiso", planTutoriaService.tienePermiso(alumno, ciclo, ds));
        model.addAttribute("esConsejero", planTutoriaService.verificarConsejero(alumno, ciclo, ds));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/consejeria/aconsejadostutor"));

        return "consejeria/agendartutorado/agendarTutorado";
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/allCitasTutorizadas")
    public DynatableResponse list(@PathVariable("idAlumno") Long idAlumno, DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CitaConsejeroAlumno> citas = service.allByDynatable(filter, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (CitaConsejeroAlumno cita : citas) {
            ObjectNode node = JaneHelper
                    .from(cita)
                    .join("consejero", "id")
                    .join("consejero.colaborador", "codigo")
                    .join("consejero.colaborador.persona", "apellidosNombres,numeroDocIdentidad,emailCompania")
                    .join("consejero.colaborador.persona.tipoDocumento", "simbolo")
                    .join("cicloAcademico", "descripcion")
                    .json();

            List<PlanTutorial> planes = cita.getPlanesTutoriales();
            ArrayNode planesJson = JaneHelper.from(planes).array();
            node.set("planesTutoriales", planesJson);

            array.add(node);
        }

        DynatableResponse json = new DynatableResponse();
        json.setFiltered(filter.getFiltered());
        json.setData(array);
        json.setTotal(filter.getTotal());

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/saveCita")
    public JsonResponse savePlan(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestBody CitaConsejeroAlumno cita, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveCitaTutorizada(cita, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("cancelarCitaTutorado")
    public JsonResponse cancelarCitaTutorado(@RequestBody CitaConsejeroAlumno cita, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.cancelarCitaTutorado(cita, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("updateCitaTutorado")
    public JsonResponse updateCitaTutorado(@RequestBody CitaConsejeroAlumno cita, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.updateCitaTutorado(cita, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }

    @ResponseBody
    @RequestMapping("postergarCitaTutorado")
    public JsonResponse postergarCitaTutorado(@RequestBody CitaConsejeroAlumno cita, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.postergarCitaTutorado(cita, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó satisfactoriamente los datos");
        json.setSuccess(true);

        return json;
    }
    
    @ResponseBody
    @RequestMapping("marcarAsistenciaCita")
    public JsonResponse marcarAsistenciaCita(@RequestBody CitaConsejeroAlumno cita, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.marcarAsistenciaCita(cita, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se marcó la asistencia satisfactoriamente");
        json.setSuccess(true);

        return json;
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
