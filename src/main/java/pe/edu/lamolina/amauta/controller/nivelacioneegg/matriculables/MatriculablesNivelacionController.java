package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/matriculablesnivelacion")
public class MatriculablesNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final MatriculablesNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/matriculablesnivelacion/matriculablesNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<NotaAlumnoNivelacion> matriculables = service.allMatriculablesByDynatable(filter, ciclo);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (NotaAlumnoNivelacion matble : matriculables) {
            ObjectNode node = JaneHelper
                    .from(matble)
                    .join("temaExamen", "id,codigo,nombre")
                    .join("curso", "id,codigo,nombre")
                    .join("cursoNivelacion", "id,codigo")
                    .join("cursoNivelacion.aula", "id,codigo")
                    .join("cursoNivelacion.grupoHoras", "id,codigo")
                    .join("alumnoNivelacion.alumno", "id,codigo")
                    .join("alumnoNivelacion.alumno.modalidadEstudio", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.carrera", "id,codigo,nombre,tipo,tipoEnum")
                    .join("alumnoNivelacion.alumno.carrera.facultad", "id,codigo,nombre")
                    .join("alumnoNivelacion.alumno.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("alumnoNivelacion.alumno.persona.tipoDocumento", "simbolo")
                    .json();

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
        MatriculablesResumen resumen = service.resumen(ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setData(JaneHelper.from(resumen).json());
        json.setSuccess(true);
        return json;
    }

    @ResponseBody
    @RequestMapping("generarMatriculables")
    public JsonResponse generarMatriculables(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int nuevos = service.generarMatriculables(ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        if (nuevos > 0) {
            json.setMessage("Se crearon " + nuevos + " matriculables");
        } else {
            json.setMessage("No se encontraron matriculables para crear");
        }

        json.setSuccess(nuevos > 0);
        return json;
    }

    @ResponseBody
    @RequestMapping("matriculaMasivaTipo1")
    public JsonResponse matriculaMasivaTipo1(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int nuevos = service.matriculaMasivaTipo1(ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        if (nuevos > 0) {
            json.setMessage("Se realizaron " + nuevos + " inscripciones");
        } else {
            json.setMessage("No se encontraron matriculables para inscribir");
        }

        json.setSuccess(nuevos > 0);
        return json;
    }

    @ResponseBody
    @RequestMapping("matricularCurso")
    public JsonResponse matricularCurso(@RequestBody NotaAlumnoNivelacion alumnoCurso, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.matricularCurso(alumnoCurso, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se matriculó satisfactoriamente");
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("retirarCurso")
    public JsonResponse retirarCurso(@RequestBody NotaAlumnoNivelacion alumnoCurso, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.retirarCurso(alumnoCurso, ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se retiró satisfactoriamente");
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
