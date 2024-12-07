package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion.CursoListTemas;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica.dto.CursoReplicaDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/cursoreplicanivelacion")
public class CursoReplicaNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    public final CursoReplicaNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/cursoreplicanivelacion/cursoreplicanivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<Curso> cursosNivelacion = service.allByDynatable(filter);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Curso curso : cursosNivelacion) {
            ObjectNode node = JaneHelper
                    .from(curso)
                    .only("id,codigo,nombre,estadoEnum,estado")
                    .join("departamentoAcademico", "id,codigo,nombre")
                    .join("departamentoAcademico.facultad", "id,codigo,nombre")
                    .json();
            List<CursoReplicaNivelacion> cursosReplica = curso.getCursosReplica();
            ArrayNode arrayReplica = new ArrayNode(JsonNodeFactory.instance);
            if (cursosReplica != null) {
                arrayReplica = this.createCursosReplicaJson(cursosReplica);
            }
            node.set("cursosReplica", arrayReplica);
            array.add(node);
        }

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("searchCurso")
    public JsonResponse searchCurso(@RequestParam String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Curso> cursos = service.allCursos(nombre);

        ArrayNode cursosJson = JaneHelper.from(cursos)
                .only("id,codigo,nombre")
                .join("cursoCicloActivo", "id,horasCiclo")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(cursosJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("saveRelacionRegular")
    public JsonResponse saveRelacion(@RequestBody CursoReplicaDTO cursoDTO, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int relacionados = service.saveRelacionRegular(cursoDTO, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage(relacionados > 0 ? "Se relaciono los curso satisfactoriamente." : "Se quitaron los temas relacionados al curso.");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ArrayNode createCursosReplicaJson(List<CursoReplicaNivelacion> cursosReplica) {

        return JaneHelper
                .from(cursosReplica)
                .join("cursoRegular", "id,codigo,nombre")
                .array();
    }

}
