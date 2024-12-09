package pe.edu.lamolina.amauta.controller.nivelacioneegg.registronotafinal;

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
import pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion.dto.ControlAsistenciaDTO;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/carganivelacion")
public class RegistroNotaFinalController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final RegistroNotaFinalService service;
    private final VerificadorService verificadorService;

    @RequestMapping("{seccion}/notas")
    public String lecciones(
            @PathVariable("seccion") Long idSeccion,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);

        model.addAttribute("seccionJson", this.createSeccionJson(seccion));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/nivelacioneegg/carganivelacion"));

        return "nivelacioneegg/leccionnivelacion/leccionNivelacion";
    }

    @ResponseBody
    @RequestMapping("{seccion}/alumnos")
    public DynatableResponse alumnos(
            @PathVariable("seccion") Long idSeccion,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);
        List<NotaAlumnoNivelacion> alumnos = service.allAlumnos(filter, seccion);

        ArrayNode array = this.createAlumnosJson(alumnos);

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("registrarNota")
    public JsonResponse registrarNota(@RequestBody NotaAlumnoNivelacion form, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        service.registrarNota(form, docente, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setMessage("Nota registrada satisfactoriamente");
        return json;
    }

    @ResponseBody
    @RequestMapping("cerrarNotas")
    public JsonResponse cerrarNotas(@RequestBody CursoNivelacion form, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        service.cerrarNotas(form, docente, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setMessage("Se cerró el ingreso de notas satisfactoriamente");
        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ObjectNode createSeccionJson(CursoNivelacion cursoNiv) {
        ObjectNode node = JaneHelper
                .from(cursoNiv)
                .only("id,codigo,matriculados")
                .join("docente", "id,codigo")
                .join("docente.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                .join("aula", "id,codigo,nombre,capacidadAula,aforo")
                .join("grupoHoras", "id,codigo")
                .join("cursoCiclo", "id,horasCiclo")
                .join("cursoCiclo.curso", "id,codigo,nombre,horasCiclo")
                .json();

        return node;
    }

    private ArrayNode createAlumnosJson(List<NotaAlumnoNivelacion> alumnos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (NotaAlumnoNivelacion leccion : alumnos) {
            ObjectNode node = JaneHelper
                    .from(leccion)
                    .join("cursoNivelacion", "id,codigo")
                    .join("alumnoNivelacion", "id,estado,estadoEnum")
                    .join("alumnoNivelacion.alumno", "id,codigo")
                    .join("alumnoNivelacion.alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumnoNivelacion.alumno.persona.tipoDocumento", "simbolo")
                    .json();

            array.add(node);
        }
        return array;
    }

    private ArrayNode createFechasJson(List<ControlAsistenciaDTO> fechas) {
        return JaneHelper
                .from(fechas)
                .array();
    }

}
