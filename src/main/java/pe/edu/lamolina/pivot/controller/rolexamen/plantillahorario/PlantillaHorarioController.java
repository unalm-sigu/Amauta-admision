package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/plantillahorario")
public class PlantillaHorarioController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlantillaHorarioService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        List<RolExamenes> rolesExamenes = service.allRolExamenesActives(ds.getCicloAcademico());
        JsonNodeFactory jc = JsonNodeFactory.instance;

        ArrayNode jRolesExamenes = new ArrayNode(jc);
        rolesExamenes.forEach(x -> {
            jRolesExamenes.add(JsonHelper.createJson(x, jc, false, new String[]{
                "*", "eventoCicloAcademico.eventoAcademico.*"
            }));
        });
        model.addAttribute("jRolesExamenes", jRolesExamenes.toString());

        return "rolexamen/plantillahorario/plantillaHorario";
    }

    @RequestMapping("{rolExamen}")
    public String indexWithRolExamen(
            @PathVariable("rolExamen") Long rolExamenId,
            Model model,
            HttpSession session) {

        RolExamenes rolExamenes = service.findRolExamenes(new RolExamenes(rolExamenId));
        ObjectNode jRolExamenes = JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false,
                new String[]{
                    "*",
                    "eventoCicloAcademico.eventoAcademico.*",
                    "semanasExamen.rolExamenes.*",
                    "semanasExamen.*",
                    "semanasExamen.horaFin",
                    "semanasExamen.horaInicio"
                });
        model.addAttribute("jRolExamenes", jRolExamenes.toString());
        return this.index(model, session);
    }

    @ResponseBody
    @RequestMapping(value = "changeRolExamen", method = RequestMethod.POST)
    public JsonResponse changeRolExamen(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            rolExamenes = service.findRolExamenes(rolExamenes);
            response.setData(JsonHelper.createJson(rolExamenes, JsonNodeFactory.instance, false, new String[]{
                "*",
                "eventoCicloAcademico.eventoAcademico.*",
                "semanasExamen.rolExamenes.*",
                "semanasExamen.*",
                "semanasExamen.horaFin.*",
                "semanasExamen.horaInicio.*"
            }));

            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("listarGruposExamenByRolExamen")
    public DynatableResponse listarGruposExamenByRolExamen(
            DynatableFilter filter,
            @RequestParam(name = "rolExamenes", required = false) Long idRolExamenes,
            HttpSession session, HttpServletRequest request) {

        DynatableResponse response = new DynatableResponse();
        JsonNodeFactory jc = JsonNodeFactory.instance;

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (idRolExamenes == null) {
                response.setData(new ArrayNode(jc));
                response.setTotal(filter.getTotal());
                response.setFiltered(filter.getFiltered());
                return response;
            }

            List<GrupoHorasExamen> gruposHorasExamenes = service.allGrupoHorasExamenByRolExamen(new RolExamenes(idRolExamenes), filter);
            ArrayNode jGruposHorasExamenes = new ArrayNode(jc);
            for (GrupoHorasExamen ghe : gruposHorasExamenes) {
                ObjectNode node = JsonHelper.createJson(ghe, jc, false,
                        new String[]{
                            "*",
                            "rolExamenes.*",
                            "grupoHoras.id",
                            "grupoHoras.codigo",
                            "grupoHoras.tipoSeccion",
                            "grupoHoras.letra",
                            "grupoHoras.color",
                            "fechasHorasGruposExamen.id",
                            "fechasHorasGruposExamen.hora.id",
                            "fechasHorasGruposExamen.hora.codigo",
                            "fechasHorasGruposExamen.dia.id",
                            "fechasHorasGruposExamen.dia.simbolo",
                            "semanaExamen.numeroSemana",
                            "semanaExamen.id"
                        });
                jGruposHorasExamenes.add(node);
            }

            response.setData(jGruposHorasExamenes);
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            response.setData(new ArrayNode(jc));
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "listarHorarioSemanal", method = RequestMethod.POST)
    public JsonResponse listarHorarioSemanal(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        JsonNodeFactory jc = JsonNodeFactory.instance;
        try {
            List<Dia> dias = service.allDias();
            List<Hora> horas = service.allHoras();
            rolExamenes = service.findRolExamenes(rolExamenes);
            List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = service.allFechaHoraGrupoExamenBySemanas(rolExamenes.getSemanasExamen());
            Map<Long, List<FechaHoraGrupoExamen>> mapFechasHorasGpoExamen = TypesUtil.convertListToMapList("semanaExamen.id", fechasHorasGrupoExamen);

            ArrayNode jSeamanasExamen = new ArrayNode(jc);
            for (SemanaExamen semanaExamen : rolExamenes.getSemanasExamen()) {
                ObjectNode jTblSeamanaExamen = this.horarioBySemanaExamen(semanaExamen, dias, horas, mapFechasHorasGpoExamen);
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
                /*
                if (semanaExamen.getNumeroSemana() == BigDecimal.ONE.intValue()) {
                    jSemanaExamen.put("selected", Boolean.TRUE);
                }*/
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

    private ObjectNode horarioBySemanaExamen(
            SemanaExamen semanaExamen,
            List<Dia> dias,
            List<Hora> horas,
            Map<Long, List<FechaHoraGrupoExamen>> mapFechasHorasGpoExamen) {

        JsonNodeFactory jc = JsonNodeFactory.instance;

        ObjectNode data = new ObjectNode(jc);
//        List<Dia> dias = service.allDias();
//        List<Hora> horas = service.allHoras();
        Map<Integer, Hora> mapHoras = TypesUtil.convertListToMap("numero", horas);

        List<Hora> horasEncontradas = horas.stream()
                .filter(x -> x.getNumero() >= semanaExamen.getHoraInicio().getNumero() && x.getNumero() < semanaExamen.getHoraFin().getNumero())
                .collect(Collectors.toList());
        Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

        ArrayNode diasJson = new ArrayNode(jc);
        for (Dia dia : dias) {
            diasJson.add(JsonHelper.createJson(dia, jc, true, new String[]{"*"}));
        }
        ArrayNode horasJson = new ArrayNode(jc);
        for (Hora horasEncontrada : horasEncontradas) {
            Hora horaSiguiente = mapHoras.get(horasEncontrada.getNumero() + 1);
            horasEncontrada.setHoraSiguiente(horaSiguiente);
            horasJson.add(JsonHelper.createJson(horasEncontrada, jc, true,
                    new String[]{
                        "*",
                        "horaSiguiente.*"
                    }));
        }
        data.set("dias", diasJson);
        data.set("horas", horasJson);

        List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = TypesUtil.getListNotNull(mapFechasHorasGpoExamen.get(semanaExamen.getId()));
//        List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = service.allFechaHoraGrupoExamenBySemanaExamen(semanaExamen);
        ObjectNode jFechasHorasGrupos = new ObjectNode(jc);
        for (FechaHoraGrupoExamen fechaHoraGrupoExamen : fechasHorasGrupoExamen) {
            ObjectNode jsonFechaHoraGrupoEach = JsonHelper.createJson(fechaHoraGrupoExamen, jc, true,
                    new String[]{"id",
                        "dia.id", "dia.nombre",
                        "hora.id", "hora.codigo", "hora.descripcion",
                        "grupoHorasExamen.*",
                        "grupoHorasExamen.grupoHoras.codigo", "grupoHorasExamen.grupoHoras.id",
                        "grupoHorasExamen.grupoHoras.tipoGrupoHoras.*"});
            jsonFechaHoraGrupoEach.put("revisado", "");
            jFechasHorasGrupos.putPOJO(fechaHoraGrupoExamen.getIdDiaHora(), jsonFechaHoraGrupoEach);
        }
        data.set("fechasHorasGrupos", jFechasHorasGrupos);
        return data;
    }

    @ResponseBody
    @RequestMapping(value = "changeSemanaExamen", method = RequestMethod.POST)
    public JsonResponse changeSemanaExamen(@RequestBody SemanaExamen semanaExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            logger.debug("changeSemanaExamen");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "calcularPlantillaHorario", method = RequestMethod.POST)
    public JsonResponse calcularPlantillaHorario(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.calcularPlantillaHorario(rolExamenes);
            logger.debug("changeSemanaExamen");
            response.setMessage("Horarios calculados correctamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "deleteFechaHoraGrupoExamen", method = RequestMethod.POST)
    public JsonResponse deleteFechaHoraGrupoExamen(@RequestBody FechaHoraGrupoExamen fechaHoraGrupoExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.deleteFechaHoraGrupoExamen(fechaHoraGrupoExamen);
            GrupoHorasExamen grupoHorasExamen = service.findGrupoHorasExamen(fechaHoraGrupoExamen.getGrupoHorasExamen());

            response.setData(JsonHelper.createJson(grupoHorasExamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "rolExamenes.*",
                        "grupoHoras.*",
                        "fechasHorasGruposExamen.*",
                        "fechasHorasGruposExamen.hora.*",
                        "fechasHorasGruposExamen.dia.*",
                        "semanaExamen.*"
                    }));

            response.setMessage("Hora removida del grupo.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "deleteGrupoHoraExamen", method = RequestMethod.POST)
    public JsonResponse deleteGrupoHoraExamen(@RequestBody GrupoHorasExamen grupoHoraExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.deleteGrupoHoraExamen(grupoHoraExamen);

            response.setMessage("Grupo eliminado satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "agregarFechaHoraGrupoExamen", method = RequestMethod.POST)
    public JsonResponse agregarFechaHoraGrupoExamen(@RequestBody FechaHoraGrupoExamen fechaHoraGrupoExamen,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            response.setSuccess(Boolean.TRUE);
            service.agregarFechoHoraGrupoExamen(fechaHoraGrupoExamen);

            GrupoHorasExamen grupoHorasExamen = service.findGrupoHorasExamen(fechaHoraGrupoExamen.getGrupoHorasExamen());

            response.setData(JsonHelper.createJson(grupoHorasExamen, JsonNodeFactory.instance, true,
                    new String[]{
                        "*",
                        "rolExamenes.*",
                        "grupoHoras.*",
                        "fechasHorasGruposExamen.*",
                        "fechasHorasGruposExamen.hora.*",
                        "fechasHorasGruposExamen.dia.*",
                        "semanaExamen.*"
                    }));

            response.setMessage("Hora agregada al grupo.");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "confirmarPlantillaHorario", method = RequestMethod.POST)
    public JsonResponse confirmarPlantillaHorario(@RequestBody RolExamenes rolExamenes,
            HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.confirmarPlantillaHorario(rolExamenes, ds);
            logger.debug("changeSemanaExamen");
            response.setMessage("Horarios confirmados correctamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
