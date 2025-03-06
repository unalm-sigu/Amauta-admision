package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnoNivelacionDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.dto.AlumnosNivelacionResumen;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnosnivelacion.helperalumnoniv.ChangeAlumnoNivelacionService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/alumnosnivelacion")
public class AlumnosNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final AlumnosNivelacionService service;
    private final ChangeAlumnoNivelacionService changeAlumnoNivelacionService;
    private final VerificadorService verificadorService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/alumnosnivelacion/alumnosNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<AlumnoNivelacion> alumnosNivelacion = new ArrayList();

        List<Carrera> carreras = new ArrayList();
        VerificadorServiceImp.CantidadItemsEnum cantidadEnum = verificadorService.verificarCantidad(TipoOficinaEnum.ESP, request, ds);

        if (cantidadEnum == VerificadorServiceImp.CantidadItemsEnum.PARCIAL) {
            carreras = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds, null);
            log.info("[list] total-acceso-carreras={}", carreras.size());
        }

        if (cantidadEnum != VerificadorServiceImp.CantidadItemsEnum.SIN_PERMISO) {
            alumnosNivelacion = service.allAlumnosByDynatable(filter, ciclo, carreras, cantidadEnum.name());
        }

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoNivelacion alumnoNiv : alumnosNivelacion) {
            ObjectNode node = JaneHelper
                    .from(alumnoNiv)
                    .join("alumno", "id,codigo")
                    .join("alumno.modalidadEstudio", "id,codigo,nombre")
                    .join("alumno.postulantePregrado.modalidadIngreso", "id,nombre")
                    .join("alumno.postulantePregrado.cicloPostula.cicloAcademico", "id,descripcion")
                    .join("alumno.carrera", "id,codigo,nombre,tipo,tipoEnum")
                    .join("alumno.carrera.facultad", "id,codigo,nombre")
                    .join("alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumno.persona.tipoDocumento", "simbolo")
                    .json();

            List<NotaAlumnoNivelacion> notasNivelaciones = alumnoNiv.getNotasNivelaciones();
            ArrayNode notasJson = JaneHelper
                    .from(notasNivelaciones)
                    .join("temaCiclo", "id,preguntas")
                    .join("temaExamen", "id,codigo,nombre")
                    .array();

            List<AlumnoNivelacionDTO> cambios = changeAlumnoNivelacionService.recrearLista(alumnoNiv.getCambios());
            ArrayNode cambiosJson = JaneHelper
                    .from(cambios)
                    .join("userRegistro", "id,google")
                    .join("userRegistro.persona", "id,nombreCompleto")
                    .array();

            node.set("notasNivelaciones", notasJson);
            node.set("cambios", cambiosJson);
            node.put("ocultar", true);
            node.put("descripcion", "");
            array.add(node);
        }

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("resumen")
    public JsonResponse resumen(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        AlumnosNivelacionResumen resumen = service.resumen(ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setData(JaneHelper.from(resumen).json());
        json.setSuccess(true);
        return json;
    }

    @ResponseBody
    @RequestMapping("createAlumnos")
    public JsonResponse createAlumnos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.createAlumnos(ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se crearon los alumnos satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("revisarTodosAlumnos")
    public JsonResponse revisarTodosAlumnos(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        int cambios = service.revisarTodosAlumnos(ciclo, ds);

        JsonResponse json = new JsonResponse();
        if (cambios > 0) {
            json.setMessage("Se realizaron " + cambios + " modificaciones");
        } else {
            json.setMessage("No se encontraron cambios que realizar");
        }

        json.setSuccess(cambios > 0);
        return json;
    }

    @ResponseBody
    @RequestMapping("revisarAlumno")
    public JsonResponse revisarAlumno(@RequestBody AlumnoNivelacion alumnoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int cambios = service.revisarAlumno(alumnoNiv, ds);

        JsonResponse json = new JsonResponse();
        if (cambios > 0) {
            json.setMessage("Se realizaron " + cambios + " modificaciones");
        } else {
            json.setMessage("No se encontraron cambios que realizar");
        }

        json.setSuccess(cambios > 0);

        return json;
    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Alumno> alumnos = service.searchAlumno(nombre, ds);

        ArrayNode jsonList = JaneHelper.from(alumnos).only("id,codigo")
                .join("persona", "numeroDocIdentidad,apellidosNombres,nombreCompleto,rutaFoto")
                .join("persona.tipoDocumento", "simbolo,nombre")
                .join("modalidadEstudio", "id,nombre")
                .join("carrera", "codigo,nombre")
                .join("carrera.facultad", "codigo,nombre")
                .join("cicloIngreso", "id,descripcion")
                .join("postulantePregrado", "id,codigo")
                .join("postulantePregrado.modalidadIngreso", "id,codigo,nombre")
                .join("postulantePregrado.cicloPostula.cicloAcademico", "id,descripcion")
                .array();

        JsonResponse response = new JsonResponse();
        response.setData(jsonList);
        response.setTotal(jsonList.size());
        response.setSuccess(true);

        return response;
    }

    @ResponseBody
    @RequestMapping("addAlumno")
    public JsonResponse addAlumno(@RequestBody Alumno alumno, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.addAlumno(alumno, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se agregó al alumno satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("deshabilitarAlumno")
    public JsonResponse deshabilitarAlumno(@RequestBody AlumnoNivelacion alumnoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.deshabilitarAlumno(alumnoNiv, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se deshabilitó al alumno satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("habilitarAlumno")
    public JsonResponse habilitarAlumno(@RequestBody AlumnoNivelacion alumnoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.habilitarAlumno(alumnoNiv, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se rehabilitó al alumno satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

}
