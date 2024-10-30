package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/cursonivelacion")
public class CursoNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final CursoNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/cursonivelacion/cursoNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<Curso> cursosNivelacion = service.allByDynatable(filter);

        ArrayNode array = JaneHelper
                .from(cursosNivelacion)
                .only("id,codigo,nombre,estadoEnum,estado")
                .join("departamentoAcademico", "id,codigo,nombre")
                .join("departamentoAcademico.facultad", "id,codigo,nombre")
                .array();

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Curso curso, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.save(curso, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage(curso.getId() == null ? "Se creo el curso satisfactoriamente" : "Se actualizo el curso satisfactoriamente.");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("activar")
    public JsonResponse changeEstado(@RequestBody Curso curso, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.changeEstado(curso, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se activo el curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(@RequestBody Curso curso, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.eliminar(curso, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se elimino el curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    @ResponseBody
    @RequestMapping("allTemas")
    public JsonResponse allTemass(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<TemaExamen> temasExamen = service.allTemas(ds);
        ArrayNode array = JaneHelper
                .from(temasExamen)
                .only("id,codigo,nombre")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("saveRelacion")
    public JsonResponse saveRelacion(@RequestBody CursoListTemas cursoListTemas, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        int relacion = service.saveRelacion(cursoListTemas, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage(relacion > 0 ? "Se relaciono los temas con el curso." : "Se quitaron los temas relacionados al curso.");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("getCursoTemas")
    public JsonResponse getCursoTemas(@RequestBody CursoListTemas cursoListTemas, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<CursoTemaExamen> cursoTemaExamenes = service.allByCurso(cursoListTemas.getCurso());
        List<Long> temasId = cursoTemaExamenes.stream().map(x -> x.getTemaExamen().getId()).collect(Collectors.toList());

        cursoListTemas.setIds(temasId);

        ObjectNode cursoJson = new ObjectNode(JsonNodeFactory.instance);
        cursoJson.put("id", cursoListTemas.getCurso().getId());
        cursoJson.put("nombre", cursoListTemas.getCurso().getNombre());

        ArrayNode temasSeleccionadas = new ArrayNode(JsonNodeFactory.instance);

        for (Long idTema : temasId) {
            temasSeleccionadas.add(idTema);
        }

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.set("curso", cursoJson);
        obj.set("ids", temasSeleccionadas);

        JsonResponse json = new JsonResponse();
        json.setData(obj);
        json.setSuccess(Boolean.TRUE);

        return json;
    }
}
