package pe.edu.lamolina.amauta.controller.nivelacioneegg.leccionnivelacion;

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
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/carganivelacion")
public class LeccionNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final LeccionNivelacionService service;
    private final VerificadorService verificadorService;

    @RequestMapping("{seccion}/lecciones")
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
    @RequestMapping("{seccion}/listLecciones")
    public DynatableResponse listLecciones(
            @PathVariable("seccion") Long idSeccion,
            DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);
        List<TemaAsistencia> lecciones = service.allLecciones(filter, seccion);

        ArrayNode array = this.createLeccionesJson(lecciones);

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("{seccion}/fechasLecciones")
    public JsonResponse fechasLecciones(
            @PathVariable("seccion") Long idSeccion,
            HttpSession session, HttpServletRequest request) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        CursoNivelacion seccion = service.findSeccion(new CursoNivelacion(idSeccion), docente, ciclo);
        List<ControlAsistenciaDTO> fechas = service.allFechasLecciones(seccion);

        ArrayNode array = this.createFechasJson(fechas);

        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("crearLeccion")
    public JsonResponse crearLeccion(@RequestBody TemaAsistencia form, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        TemaAsistencia tema = service.crearLeccion(form, docente, ciclo, ds);

        ObjectNode node = JaneHelper.from(tema).json();

        JsonResponse json = new JsonResponse();
        json.setSuccess(Boolean.TRUE);
        json.setMessage("Se ha creado satisfactoriamente la lección");
        json.setData(node);
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

    private ArrayNode createLeccionesJson(List<TemaAsistencia> lecciones) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (TemaAsistencia leccion : lecciones) {
            ObjectNode node = JaneHelper
                    .from(leccion)
                    .join("cursoNivelacion", "id,codigo")
                    .join("horaInicio")
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
