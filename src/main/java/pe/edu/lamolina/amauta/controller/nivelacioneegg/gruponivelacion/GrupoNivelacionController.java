package pe.edu.lamolina.amauta.controller.nivelacioneegg.gruponivelacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("nivelacioneegg/gruponivelacion")
public class GrupoNivelacionController {

    public final String rutaModulo = this.getClass().getAnnotation(RequestMapping.class).value()[0];

    private final GrupoNivelacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloJson", this.createCicloJson(ciclo));
        model.addAttribute("rutaModulo", rutaModulo);

        return "nivelacioneegg/gruponivelacion/grupoNivelacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<GrupoNivelacion> grupos = service.allByDynatable(filter, ciclo, ds);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (GrupoNivelacion grupo : grupos) {
            ObjectNode node = JaneHelper
                    .from(grupo)
                    .only("id,codigo,tipo,orden")
                    .json();
            node.put("conHorario", !grupo.getHorariosGrupo().isEmpty());
            node.put("horas", grupo.getHorariosGrupo().size());
            array.add(node);
        }

        DynatableResponse json = new DynatableResponse();
        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("saveGrupo")
    public JsonResponse saveGrupo(@RequestBody GrupoNivelacion grupo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        if(grupo.getId() == null){
            service.saveGrupo(grupo, ds);
        } else {
            service.updateGrupo(grupo, ds.getCicloAcademico(), ds);
        }

        JsonResponse json = new JsonResponse();
        json.setMessage(grupo.getId() == null ? "Se creó el grupo satisfactoriamente" : "Se actualizó el grupo satisfactoriamente.");
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("saveHorarioGrupo")
    public JsonResponse saveHorarioGrupo(@RequestBody GrupoHorarioPayload payload, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveHorarioGrupo(payload.getId(), payload.getHorarios(), ds.getCicloAcademico(), ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se guardó el horario satisfactoriamente.");
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("getHorarioGrupo")
    public JsonResponse getHorarioGrupo(@RequestBody GrupoNivelacion grupo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<HorarioGrupoNivelacion> horarios = service.getHorarioGrupo(grupo.getId(), ds.getCicloAcademico());
        
        ArrayNode array = JaneHelper
                .from(horarios)
                .only("id,dia,hora")
                .join("dia", "id,nombre,numeroDia,simbolo")
                .join("hora", "id,descripcion,descripcion2,numero")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("getHorarioOtrosGrupos")
    public JsonResponse getHorarioOtrosGrupos(@RequestBody GrupoNivelacion grupo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<HorarioGrupoNivelacion> horarios = service.getHorarioOtrosGrupos(grupo.getId(), ds.getCicloAcademico());

        ArrayNode array = JaneHelper
                .from(horarios)
                .only("id,dia,hora")
                .join("grupoNivelacion", "id,codigo")
                .join("dia", "id,nombre,numeroDia,simbolo")
                .join("hora", "id,descripcion,descripcion2,numero")
                .array();

        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("eliminarGrupo")
    public JsonResponse eliminarGrupo(@RequestBody GrupoNivelacion grupo, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.eliminarGrupo(grupo, ds);

        JsonResponse json = new JsonResponse();
        json.setMessage("Se eliminó el grupo satisfactoriamente");
        json.setSuccess(Boolean.TRUE);

        return json;
    }

    @ResponseBody
    @RequestMapping("allDias")
    public JsonResponse allDias() {
        List<Dia> dias = service.allDias();
        ArrayNode array = JaneHelper.from(dias).only("id,nombre,numeroDia,simbolo").array();
        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    @ResponseBody
    @RequestMapping("allHoras")
    public JsonResponse allHoras() {
        List<Hora> horas = service.allHoras();
        ArrayNode array = JaneHelper.from(horas).only("id,descripcion,descripcion2,numero").array();
        JsonResponse json = new JsonResponse();
        json.setData(array);
        json.setSuccess(Boolean.TRUE);
        return json;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        return JaneHelper
                .from(ciclo)
                .only("id,descripcion,descripcion2")
                .json();
    }

    @Data
    public static class GrupoHorarioPayload {
        private Long id;
        private List<HorarioGrupoNivelacion> horarios;
    }
}
