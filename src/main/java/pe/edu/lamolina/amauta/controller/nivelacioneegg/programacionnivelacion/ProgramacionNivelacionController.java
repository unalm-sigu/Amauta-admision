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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCurso;
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
        List<GrupoHorasNivelacion> gruposHoras = service.allGruposHoras();

        model.addAttribute("gruposHorasJson", this.createGruposJson(gruposHoras));
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

        ArrayNode array = this.createCursosNivJson(cursosNivela);

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
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<Curso> cursos = service.allCursos(nombre, ciclo);

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
    @RequestMapping("getHorario")
    public JsonResponse getHorario(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<HorarioCurso> horarios = service.getHorario(cursoNiv, ciclo);
        PeriodoDTO periodo = service.getPeriodo(cursoNiv, ciclo);

        ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
        data.set("horario", this.createHorariosCursosJson(horarios));
        data.set("periodo", JaneHelper.from(periodo).json());

        JsonResponse json = new JsonResponse();
        json.setData(data);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("getHorarioGrupo")
    public JsonResponse getHorarioGrupo(@RequestBody GrupoHorasNivelacion gproHoras, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<HorarioCurso> horarios = service.getHorarioGrupo(gproHoras, ciclo);
        ArrayNode horariosJson = this.createHorariosCursosJson(horarios);

        JsonResponse json = new JsonResponse();
        json.setData(horariosJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("getPeriodo")
    public JsonResponse getPeriodo(@RequestBody PeriodoDTO periodo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        periodo.calcular();
        ObjectNode horariosJson = JaneHelper.from(periodo).json();

        JsonResponse json = new JsonResponse();
        json.setData(horariosJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("searchAula")
    public JsonResponse searchAula(@RequestParam String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Aula> aulas = service.allAulas(nombre);

        ArrayNode aulasJson = JaneHelper.from(aulas)
                .only("id,codigo,nombre,aforo,capacidadAula")
                .join("aulaSuperior", "id,codigo,nombre")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(aulasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("verificarCruceAula")
    public JsonResponse verificarCruceAula(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String mensaje = service.verificarCruceAula(cursoNiv, ds.getCicloAcademico());

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("hayCruceAula", mensaje != null);
        node.put("mensajeCruceAula", mensaje);

        JsonResponse json = new JsonResponse();
        json.setData(node);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("searchDocente")
    public JsonResponse searchDocente(@RequestParam String nombre, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Docente> docentes = service.allDocentes(nombre);

        ArrayNode cursosJson = JaneHelper.from(docentes)
                .only("id,codigo")
                .join("departamentoAcademico", "codigo,nombre")
                .join("departamentoAcademico.facultad", "codigo,nombre")
                .join("persona", "id,apellidosNombres,numeroDocIdentidad")
                .join("persona.tipoDocumento", "simbolo")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(cursosJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("verificarCruceDocente")
    public JsonResponse verificarCruceDocente(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String mensaje = service.verificarCruceDocente(cursoNiv, ds.getCicloAcademico());

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("hayCruceDocente", mensaje != null);
        node.put("mensajeCruceDocente", mensaje);

        JsonResponse json = new JsonResponse();
        json.setData(node);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("addCurso")
    public JsonResponse addCurso(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.addCurso(cursoNiv, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se agregó el curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("setHorario")
    public JsonResponse setHorario(@RequestBody CursoCicloAcademico cursoCiclo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        service.setHorario(cursoCiclo, ciclo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se asignó el horario satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("changeGrupo")
    public JsonResponse changeGrupo(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.changeGrupo(cursoNiv, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se modificó el grupo del curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("changeAula")
    public JsonResponse changeAula(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.changeAula(cursoNiv, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se modificó el aula del curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("changeDocente")
    public JsonResponse changeDocente(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.changeDocente(cursoNiv, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se modificó el docente del curso satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("changeEstado/{estado}")
    public JsonResponse changeEstado(
            @PathVariable("estado") String estado,
            @RequestBody CursoNivelacion cursoNiv, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        SeccionEstadoEnum estadoEnum = SeccionEstadoEnum.valueOf(estado);
        service.changeEstado(cursoNiv, estadoEnum, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se modificó el estado de la sección satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

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
        List<Hora> horas = service.allHoras();
        ArrayNode horasJson = JaneHelper.from(horas).array();

        JsonResponse json = new JsonResponse();
        json.setData(horasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("allSemanas")
    public JsonResponse allSemanas(@RequestBody CursoNivelacion cursoNiv, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<PeriodoDTO> semanas = service.allSemanas(cursoNiv, ds);
        ArrayNode semnasJson = JaneHelper.from(semanas).array();

        JsonResponse json = new JsonResponse();
        json.setData(semnasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("addSemana/{direccion}")
    public JsonResponse addSemana(
            @PathVariable("direccion") String direccion,
            @RequestBody List<PeriodoDTO> semanasForm, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<PeriodoDTO> semanas = service.addSemana(semanasForm, direccion);
        ArrayNode semnasJson = JaneHelper.from(semanas).array();

        JsonResponse json = new JsonResponse();
        json.setData(semnasJson);
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    private ArrayNode createGruposJson(List<GrupoHorasNivelacion> grupos) {
        return JaneHelper.from(grupos).array();
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    private ArrayNode createHorariosCursosJson(List<HorarioCurso> horarios) {
        return JaneHelper
                .from(horarios)
                .only("id,semana")
                .join("dia", "id,nombre,simbolo")
                .join("hora", "id,codigo,numero,descripcion")
                .join("curso", "id,codigo,nombre")
                .array();
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

            List<HorarioCurso> horarios = cursoNiv.getHorariosCurso();
            ArrayNode horarioJson = this.createHorariosCursosJson(horarios);

            node.set("horariosCurso", horarioJson);
            array.add(node);
        }
        return array;
    }

}
