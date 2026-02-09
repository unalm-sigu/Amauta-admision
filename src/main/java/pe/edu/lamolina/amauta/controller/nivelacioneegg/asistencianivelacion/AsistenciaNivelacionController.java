package pe.edu.lamolina.amauta.controller.nivelacioneegg.asistencianivelacion;

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
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/carganivelacion")
public class AsistenciaNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final AsistenciaNivelacionService service;
    private final VerificadorService verificadorService;

    @RequestMapping("{leccion}/asistencia")
    public String asistencia(
            @PathVariable("leccion") Long idLeccion,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        TemaAsistencia leccion = docente == null ? service.findLeccion(new TemaAsistencia(idLeccion), ciclo)
                : service.findLeccion(new TemaAsistencia(idLeccion), docente, ciclo);

        model.addAttribute("leccionJson", this.createLeccionJson(leccion));
        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("esDocente", docente != null);
        model.addAttribute("rutaModulo", rutaModulo);
        model.addAttribute("origen", verificadorService.getOrigen(origen, "/nivelacioneegg/carganivelacion"));

        return "nivelacioneegg/asistencianivelacion/asistenciaNivelacion";
    }

    @ResponseBody
    @RequestMapping("{leccion}/asistentes")
    public DynatableResponse asistentes(
            @PathVariable("leccion") Long idLeccion,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        TemaAsistencia leccion = docente == null ? service.findLeccion(new TemaAsistencia(idLeccion), ciclo)
                : service.findLeccion(new TemaAsistencia(idLeccion), docente, ciclo);
        List<AsistenciaNivelacion> asistentes = service.allInscritos(filter, leccion);

        ArrayNode array = this.createAsistentesJson(asistentes);

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping("findLeccion")
    public JsonResponse findLeccion(@RequestBody TemaAsistencia form, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        TemaAsistencia leccion = service.findLeccion(form, docente, ciclo);
        ObjectNode data = this.createLeccionJson(leccion);

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setData(data);
        return json;
    }

    @ResponseBody
    @RequestMapping("marcarAsistencia")
    public JsonResponse marcarAsistencia(@RequestBody AsistenciaNivelacion asistencia, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();
        service.marcarAsistencia(asistencia, docente, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se registró la " + asistencia.getEstadoEnum().getMensaje());
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ArrayNode createAsistentesJson(List<AsistenciaNivelacion> asistentes) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AsistenciaNivelacion asiste : asistentes) {
            ObjectNode node = JaneHelper
                    .from(asiste)
                    .join("temaAsistencia", "id,fecha,temaClase")
                    .join("alumnoNivelacion", "id,estado")
                    .join("alumnoNivelacion.alumno", "id,codigo")
                    .join("alumnoNivelacion.alumno.modalidadEstudio", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.carrera", "id,codigo,nombre,tipo,tipoEnum")
                    .join("alumnoNivelacion.alumno.carrera.facultad", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumnoNivelacion.alumno.persona.tipoDocumento", "simbolo")
                    .json();

            array.add(node);
        }
        return array;
    }

    private ObjectNode createLeccionJson(TemaAsistencia leccion) {
        ObjectNode node = JaneHelper
                .from(leccion)
                .join("cursoNivelacion", "id,codigo,matriculados")
                .join("cursoNivelacion.docente", "id,codigo")
                .join("cursoNivelacion.docente.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                .join("cursoNivelacion.aula", "id,codigo,nombre,capacidadAula,aforo")
                .join("cursoNivelacion.plantilla", "id,codigo")
                .join("cursoNivelacion.cursoCiclo", "id,horasCiclo")
                .join("cursoNivelacion.cursoCiclo.curso", "id,codigo,nombre,horasCiclo")
                .json();

        return node;
    }

}
