package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

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
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.TipoAtencionTutorado;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("consejeria/aconsejadostutor")
public class DerivarTutoradoController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final DerivarTutoradoService service;
    private final PlanTutoriaService planTutoriaService;
    private final VerificadorService verificadorService;

    @RequestMapping("{idAlumno}/derivarTutorado")
    public String index(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Alumno alumno = planTutoriaService.findAlumno(new Alumno(idAlumno));
        AlumnoConsejero alumnoConsejero = planTutoriaService.findAlumnoConsejero(alumno, ciclo);
        List<TipoAtencionTutorado> tiposAtenciones = service.allTiposAtenciones();

        model.addAttribute("tiposAtencionesJson", this.createTiposAtencionesJson(tiposAtenciones));
        model.addAttribute("alumnoJson", this.createAlumnoJson(alumno));
        model.addAttribute("consejeroJson", this.createConsejeroJson(alumnoConsejero.getConsejero()));
        model.addAttribute("alumnoConsejeroJson", this.createAlumnoConsejeroJson(alumnoConsejero));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("tienePermiso", planTutoriaService.tienePermiso(alumno, ciclo, ds));
        model.addAttribute("esConsejero", planTutoriaService.verificarConsejero(alumno, ciclo, ds));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/consejeria/aconsejadostutor"));

        return "consejeria/derivartutorado/derivarTutorado";
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/allDerivaciones")
    public DynatableResponse allDerivaciones(@PathVariable("idAlumno") Long idAlumno, DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<AlumnoDerivadoAtencion> derivaciones = service.allByDynatable(filter, new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoDerivadoAtencion derivacion : derivaciones) {
            ObjectNode node = JaneHelper
                    .from(derivacion)
                    .join("tipoAtencionTutorado", "id,codigo,nombre")
                    .join("personaRemitente", "apellidosNombres,numeroDocIdentidad,emailCompania")
                    .join("personaRemitente.tipoDocumento", "simbolo")
                    .join("tipoRemitenteDerivacion", "nombre")
                    .join("especialidadMedica", "id,nombre")
                    .join("consejero", "id")
                    .join("consejero.colaborador.persona", "apellidosNombres,emailCompania")
                    .join("medico", "id")
                    .join("medico.colaborador.persona", "apellidosNombres,emailCompania")
                    .join("colaborador", "id")
                    .join("colaborador.persona", "apellidosNombres,emailCompania")
                    .join("colaborador.cargo", "nombre")
                    .join("colaborador.oficina", "nombre")
                    .join("colaborador.oficina.oficinaPrincipal", "nombre")
                    .join("curso", "id,tpc,codigo,nombre")
                    .join("cicloAcademico", "id")
                    .join("alumno", "id")
                    .json();

            array.add(node);
        }

        DynatableResponse json = new DynatableResponse();
        json.setFiltered(filter.getFiltered());
        json.setData(array);
        json.setTotal(filter.getTotal());

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/allCursosMatriculados")
    public JsonResponse allCursosMatriculados(@PathVariable("idAlumno") Long idAlumno, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Curso> cursos = service.allCursosMatriculados(new Alumno(idAlumno), ds.getCicloAcademico(), ds);

        ArrayNode array = JaneHelper
                .from(cursos)
                .only("id,codigo,tpc,nombre")
                .array();

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setData(array);
        json.setTotal(cursos.size());

        return json;
    }

    @ResponseBody
    @RequestMapping("{idAlumno}/saveDerivacion")
    public JsonResponse saveDerivacion(
            @PathVariable("idAlumno") Long idAlumno,
            @RequestBody AlumnoDerivadoAtencion derivacion, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveDerivacion(new Alumno(idAlumno), derivacion, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setMessage("Se guardó satisfactoriamente la derivación");

        return json;
    }

    private ArrayNode createTiposAtencionesJson(List<TipoAtencionTutorado> tipos) {
        return JaneHelper
                .from(tipos)
                .only("id,codigo,grupoAtencion,nombre")
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
