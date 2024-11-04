package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion;

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
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/programacionnivelacion")
public class ProgramacionNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final ProgramacionNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/programacionnivelacion/programacionNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<CursoNivelacion> cursosNivela = service.allCursosNivelacionByDynatable(filter, ciclo);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (CursoNivelacion cursoNiv : cursosNivela) {
            ObjectNode node = JaneHelper
                    .from(cursoNiv)
                    .join("docente", "id,codigo")
                    .join("docente.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("aula", "id,codigo,nombre")
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
    @RequestMapping("addCurso")
    public JsonResponse addCurso(@RequestBody CursoNivelacion cursoNivelacion, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.addCurso(cursoNivelacion, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se agregó el curso satisfactoriamente");
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
