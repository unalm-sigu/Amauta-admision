package pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.PeriodoDTO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/carganivelacion")
public class CargaNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final CargaNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/carganivelacion/cargaNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();
        List<CursoNivelacion> cursosNivela = service.allCargaAcademica(filter, ciclo, docente);

        ArrayNode array = this.createCursosNivJson(cursosNivela);

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("allDias")
    public JsonResponse allDias(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Dia> dias = service.allDias();
        ArrayNode diasJson = JaneHelper.from(dias).array();

        JsonResponse json = new JsonResponse();
        json.setData(diasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("allHoras")
    public JsonResponse allHoras(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Hora> horas = service.allHoras(ds.getDocente(), ds.getCicloAcademico());
        ArrayNode horasJson = JaneHelper.from(horas).array();

        JsonResponse json = new JsonResponse();
        json.setData(horasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("allSemanas")
    public JsonResponse allSemanas(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<PeriodoDTO> semanas = service.allSemanas(ds.getDocente(), ds.getCicloAcademico());
        ArrayNode semnasJson = JaneHelper.from(semanas).array();

        JsonResponse json = new JsonResponse();
        json.setData(semnasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("getHorario")
    public JsonResponse getHorario(HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Docente docente = ds.getDocente();

        List<HorarioAula> horarios = service.getHorarioGrupo(docente, ciclo);
        ArrayNode horariosJson = this.createHorariosCursosJson(horarios);

        JsonResponse json = new JsonResponse();
        json.setData(horariosJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ArrayNode createHorariosCursosJson(List<HorarioAula> horarios) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (HorarioAula horario : horarios) {
            ObjectNode node = JaneHelper
                    .from(horario)
                    .only("id,fechaInicio")
                    .join("aula", "id,codigo,nombre")
                    .join("dia", "id,nombre,simbolo")
                    .join("hora", "id,codigo,numero,descripcion")
                    .join("cursoNivelacion", "id,codigo")
                    .json();

            Curso curso = horario.getCursoNivelacion().getCursoCiclo().getCurso();
            ObjectNode cursoJson = JaneHelper
                    .from(curso)
                    .only("id,codigo,nombre")
                    .json();

            node.set("curso", cursoJson);
            array.add(node);
        }

        return array;
    }

    private ArrayNode createCursosNivJson(List<CursoNivelacion> cursosNivela) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (CursoNivelacion cursoNiv : cursosNivela) {
            ObjectNode node = JaneHelper
                    .from(cursoNiv)
                    .join("docente", "id,codigo")
                    .join("docente.persona", "id,apellidosNombres,numeroDocIdentidad,tipoFoto,rutaFoto")
                    .join("aula", "id,codigo,nombre,capacidadAula,aforo")
                    .join("aula.aulaSuperior", "id,codigo,nombre")
                    .join("grupoHoras", "id,codigo")
                    .join("cursoCiclo", "id,horasCiclo")
                    .join("cursoCiclo.curso", "id,codigo,nombre,horasCiclo")
                    .json();
            array.add(node);
        }
        return array;
    }

}
