package pe.edu.lamolina.pivot.controller.rolexamen.docente;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario.PlantillaHorarioService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/docentes")
public class RolExamenDocenteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenDocenteService service;

    @Autowired
    PlantillaHorarioService plantillaHorarioService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("docente", JsonHelper.createJson(ds.getDocente(), JsonNodeFactory.instance, new String[]{
            "*",
            "persona.*",}));

        return "rolexamen/docente/rolExamenDocente";
    }

    @ResponseBody
    @RequestMapping("list")
    public JsonResponse list(HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            List<RolExamenDocente> examenDocentes = service.listExamenDocente(ds.getDocente(), ds.getCicloAcademico());

            response.setData(jsonExamenDocente(examenDocentes));
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "plantilla/{rolExamenId}", method = RequestMethod.POST)
    public JsonResponse plantilla(@PathVariable Long rolExamenId, @RequestBody GrupoHorasExamen grupoHorasExamen, HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonNodeFactory jc = JsonNodeFactory.instance;
        try {
            RolExamenes rolExamenes = plantillaHorarioService.findRolExamenes(new RolExamenes(rolExamenId));
            ArrayNode jSeamanasExamen = new ArrayNode(jc);
            for (SemanaExamen semanaExamen : rolExamenes.getSemanasExamen()) {
                ObjectNode jTblSeamanaExamen = this.horarioBySemanaExamen(semanaExamen, grupoHorasExamen.getGrupoHorasExamens());
                ObjectNode jSemanaExamen = JsonHelper.createJson(semanaExamen, jc, false,
                        new String[]{
                            "*",
                            "fechaInicio.*",
                            "fechaFin.*",
                            "horaInicio.*",
                            "horaFin.*"
                        });
                jSemanaExamen.set("tblHorarioSeamanaExamen", jTblSeamanaExamen);
                jSemanaExamen.put("selected", Boolean.FALSE);
                jSeamanasExamen.add(jSemanaExamen);
            }

            response.setData(jSeamanasExamen);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    private ArrayNode jsonExamenDocente(List<RolExamenDocente> examenDocentes) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arrayAulas = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arraySecciones = new ArrayNode(JsonNodeFactory.instance);

        for (RolExamenDocente examenDocente : examenDocentes) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode = JsonHelper.createJson(examenDocente, JsonNodeFactory.instance, new String[]{
                "*",
                "curso.*",
                "grupoHorasExamen.*",
                "grupoHorasExamen.horaInicio.*",
                "grupoHorasExamen.horaFin.*",
                "grupoHorasExamen.dia.*",
                "grupoHorasExamen.grupoHoras.*",
                "seccion.grupoHoras.codigo",
                "seccion.*",
                "aula.*"
            });
            if (examenDocente.getAulas() != null) {
                for (Aula aula : examenDocente.getAulas()) {
                    arrayAulas.add(JsonHelper.createJson(aula, JsonNodeFactory.instance, new String[]{"*"}));
                }
                objectNode.set("aulas", arrayAulas);
            }
            if (examenDocente.getSecciones() != null) {
                for (Seccion seccione : examenDocente.getSecciones()) {
                    arraySecciones.add(JsonHelper.createJson(seccione, JsonNodeFactory.instance, new String[]{"*"}));
                }
                objectNode.set("secciones", arraySecciones);
            }
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    private ObjectNode horarioBySemanaExamen(SemanaExamen semanaExamen, List<GrupoHorasExamen> grupoHorasExamens) {
        JsonNodeFactory jc = JsonNodeFactory.instance;

        ObjectNode data = new ObjectNode(jc);

        List<Dia> dias = plantillaHorarioService.allDias();
        List<Hora> horas = plantillaHorarioService.allHoras();
        List<Hora> horasEncontradas = horas.stream()
                .filter(x -> x.getNumero() >= semanaExamen.getHoraInicio().getNumero() && x.getNumero() <= semanaExamen.getHoraFin().getNumero())
                .collect(Collectors.toList());
        Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

        ArrayNode diasJson = new ArrayNode(jc);
        for (Dia dia : dias) {
            diasJson.add(JsonHelper.createJson(dia, jc, true, new String[]{"*"}));
        }
        ArrayNode horasJson = new ArrayNode(jc);
        for (Hora horasEncontrada : horasEncontradas) {
            horasJson.add(JsonHelper.createJson(horasEncontrada, jc, true, new String[]{"*"}));
        }
        data.set("dias", diasJson);
        data.set("horas", horasJson);

        List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = service.allFechaHoraGrupoExamenBySemanaExamen(semanaExamen, grupoHorasExamens);
        ObjectNode jFechasHorasGrupos = new ObjectNode(jc);
        for (FechaHoraGrupoExamen fechaHoraGrupoExamen : fechasHorasGrupoExamen) {
            ObjectNode jsonFechaHoraGrupoEach = JsonHelper.createJson(fechaHoraGrupoExamen, jc, true,
                    new String[]{"id",
                        "dia.id", "dia.nombre",
                        "hora.id", "hora.codigo", "hora.descripcion",
                        "grupoHorasExamen.*",
                        "grupoHorasExamen.grupoHoras.codigo", "grupoHorasExamen.grupoHoras.id",
                        "grupoHorasExamen.grupoHoras.tipoGrupoHoras.*",
                        "semanaExamen.*"
                    });
            jFechasHorasGrupos.putPOJO(fechaHoraGrupoExamen.getIdDiaHora(), jsonFechaHoraGrupoEach);
        }
        data.set("fechasHorasGrupos", jFechasHorasGrupos);
        return data;
    }
}
