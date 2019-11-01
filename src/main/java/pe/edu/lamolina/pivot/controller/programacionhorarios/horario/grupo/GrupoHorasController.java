package pe.edu.lamolina.pivot.controller.programacionhorarios.horario.grupo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.LetraGrupoHoraEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorariosEnum;
import pe.edu.lamolina.model.enums.TipoSeccionGrupoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horario/grupo")
public class GrupoHorasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoHorasService service;

    @Autowired
    SpringTemplateEngine springHtml;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });
        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "{tipo}")
    public String index(@PathVariable("tipo") Long idTipoGrupo, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<Dia> dias = service.allDia();
        List<Hora> horas = service.allHora();
        TipoGrupoHoras tipoGrupoHoras = service.findTipoGrupoHoras(idTipoGrupo);
        TipoGrupoHoras tipoGpoReg = service.findTipoGpoRegular();
        List<DiaHoraGrupo> diasHorasGposReg = service.allDiaHoraGrupoByTipo(tipoGpoReg, ciclo);

        model.addAttribute("tipoGrupoHoras", tipoGrupoHoras);
        model.addAttribute("tipoGpoJson", createTipoGpoJson(tipoGrupoHoras).toString());
        model.addAttribute("diasJson", createDiasJson(dias).toString());
        model.addAttribute("horasJson", createHorasJson(horas).toString());
        model.addAttribute("horarioRegularJson", createDiaHoraGpoJson(diasHorasGposReg).toString());
        model.addAttribute("tipoHorariosJson", JsonHelper.enumToJson(TipoGrupoHorariosEnum.values()).toString());
        model.addAttribute("ciclo", ciclo.getDescripcion());
        return "academico/horario/grupo/grupo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<GrupoHoras> grupos = service.allGrupoHoras(filter, cicloAcademico);
            List<DiaHoraGrupo> horas = service.allDiaHoraGrupo(grupos, cicloAcademico);
            Map<Long, List<DiaHoraGrupo>> mapGrupohoras = TypesUtil.convertListToMapList("grupoHorario.id", horas);
            for (GrupoHoras grupo : grupos) {

                ObjectNode node = JsonHelper.createJson(grupo, JsonNodeFactory.instance, new String[]{
                    "*",
                    "tipoGrupoHoras.*",});

                List<DiaHoraGrupo> mapGrupohora = mapGrupohoras.get(grupo.getId());
                node.put("estado", "default");
                node.put("horas", 0);
                if (mapGrupohora != null) {
                    node.put("horas", mapGrupohora.size());
                }

                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody GrupoHoras grupoHoras, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            GrupoHoras grupoCode = service.findGrupoHorasByCode(grupoHoras.getCodigo());
            GrupoHorasExcluido grupoHide = service.findGrupoHorasOculto(grupoCode, cicloAcademico);
            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            response.setSuccess(Boolean.TRUE);

            if (grupoHide != null) {
                response.setMessage("Este Grupo-Horas se encuentra oculto para este ciclo. No puede ser modificado.");
                response.setSuccess(Boolean.FALSE);

            } else if (grupoHoras.getId() != null) {
                if (grupoCode != null) {
                    if (grupoCode.getId() == grupoHoras.getId().longValue()) {
                        service.update(grupoHoras);
                        response.setMessage("Grupo Horas actualizado satisfactoriamente");
                    } else {
                        data.put("existecodigo", true);
                        response.setMessage("Grupo Horas con código ya registrado");
                        response.setSuccess(Boolean.FALSE);
                    }
                } else {
                    service.update(grupoHoras);
                    response.setMessage("Grupo Horas actualizado satisfactoriamente");
                    response.setSuccess(Boolean.TRUE);
                }
            } else {
                if (grupoCode == null) {
                    service.save(grupoHoras);
                    response.setMessage("Grupo Horas creado satisfactoriamente");
                    response.setSuccess(Boolean.TRUE);
                } else {
                    data.put("existecodigo", true);
                    response.setMessage("Grupo Horas con código ya registrado");
                    response.setSuccess(Boolean.FALSE);
                }
            }
            response.setData(data);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(GrupoHoras grupoHoras, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            GrupoHoras grupoDb = service.findGrupoHoras(grupoHoras);
            Context ctx = new Context();
            ctx.setVariable("grupoHoras", grupoDb);
            ctx.setVariable("tipoCiclos", TipoCicloEnum.values());
            ctx.setVariable("tipoSecciones", TipoSeccionGrupoEnum.values());
            ctx.setVariable("tipoHorarios", TipoGrupoHorariosEnum.values());
            ctx.setVariable("letras", LetraGrupoHoraEnum.values());
            String htmlContent = springHtml.process("academico/horario/grupo/grupoForm", ctx);
            response.setData(htmlContent);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("nuevo")
    public JsonResponse nuevo(Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            Context ctx = new Context();
            ctx.setVariable("grupoHoras", new GrupoHoras());
            ctx.setVariable("tipoCiclos", TipoCicloEnum.values());
            ctx.setVariable("tipoSecciones", TipoSeccionGrupoEnum.values());
            ctx.setVariable("tipoHorarios", TipoGrupoHorariosEnum.values());
            ctx.setVariable("letras", LetraGrupoHoraEnum.values());
            String htmlContent = springHtml.process("academico/horario/grupo/grupoForm", ctx);
            response.setData(htmlContent);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(@RequestBody GrupoHoras grupoHoras, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.delete(grupoHoras, ciclo, ds);

            response.setMessage("Grupo-horarios eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, "Este grupo está relacionado a otros elementos del sistema. No puede ser eliminado.");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("ocultar")
    public JsonResponse ocultar(@RequestBody GrupoHoras grupoHoras, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            service.ocultar(grupoHoras, ciclo, ds);

            response.setMessage("Grupo-horario ocultado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("horario")
    public JsonResponse horario(GrupoHoras grupoHoras, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        long t1 = System.currentTimeMillis();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            GrupoHoras grupoBD = service.findGrupoHoras(grupoHoras);
            List<DiaHoraGrupo> diasHorasGpo = service.allDiaHoraGrupoByGrupo(grupoBD, cicloAcademico);

            TipoGrupoHoras tipoGpoReg = service.findTipoGpoRegular();
            List<DiaHoraGrupo> diasHorasGposReg = service.allDiaHoraGrupoByTipo(tipoGpoReg, cicloAcademico);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.set("horarioGpo", createDiaHoraGpoJson(diasHorasGpo));
            node.set("horarioRegular", createDiaHoraGpoJson(diasHorasGposReg));

            response.setData(node);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("horario entregado en " + (t2 - t1) + " mseg");

        return response;
    }

    @ResponseBody
    @RequestMapping("asignarHora")
    public JsonResponse asignarHora(@RequestBody DiaHoraGrupo diaHoraGrupo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            diaHoraGrupo.setCicloAcademico(cicloAcademico);
            service.saveDiaHoraGrupo(diaHoraGrupo);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se asignó el horario satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("desasignarHora")
    public JsonResponse desasignarHora(@RequestBody DiaHoraGrupo diaHoraGrupo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            diaHoraGrupo.setCicloAcademico(cicloAcademico);
            service.desasignarHora(diaHoraGrupo, cicloAcademico);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se retiró el horario satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("clonarGrupos")
    public JsonResponse clonar(@RequestBody CicloAcademico cicloOrigen, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.clonar(cicloOrigen, ds.getCicloAcademico());
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se clonó el horario satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("listOcultos")
    public JsonResponse listOcultos(@RequestBody TipoGrupoHoras tipoGpo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            List<GrupoHoras> gpos = service.allOcultosByCiclo(tipoGpo, cicloAcademico);
            ArrayNode array = createGposHorasJson(gpos);

            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activarOcultos")
    public JsonResponse activarOcultos(@RequestBody List<GrupoHoras> gpos, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            service.activarGrupos(gpos, cicloAcademico);

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Grupos-Horas activados satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("gencolor")
    public JsonResponse gencolor(HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            service.gencolor();
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createTipoGpoJson(TipoGrupoHoras tipoGpo) {
        ObjectNode node = JsonHelper.createJson(tipoGpo, JsonNodeFactory.instance, true, new String[]{"*"});
        return node;
    }

    private ArrayNode createDiasJson(List<Dia> dias) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Dia dia : dias) {
            ObjectNode node = JsonHelper.createJson(dia, JsonNodeFactory.instance, true, new String[]{"*"});
            array.add(node);
        }
        return array;
    }

    private ArrayNode createHorasJson(List<Hora> horas) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Hora hora : horas) {
            ObjectNode node = JsonHelper.createJson(hora, JsonNodeFactory.instance, true, new String[]{"*"});
            array.add(node);
        }
        return array;
    }

    private ArrayNode createGposHorasJson(List<GrupoHoras> gposHoras) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (GrupoHoras gpoHoras : gposHoras) {
            ObjectNode node = JsonHelper.createJson(gpoHoras, JsonNodeFactory.instance, true,
                    new String[]{"*", "tipoGrupoHoras.*"}
            );
            array.add(node);
        }
        return array;
    }

    private ObjectNode createDiaHoraGpoJson(List<DiaHoraGrupo> diasHorasGpos) {
        //ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        Map<String, List<DiaHoraGrupo>> mapDiaHora = TypesUtil.convertListToMapList("key", diasHorasGpos);
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        for (Map.Entry<String, List<DiaHoraGrupo>> entry : mapDiaHora.entrySet()) {
            List<GrupoHoras> gpos = entry.getValue().stream().map(x -> x.getGrupoHorario()).collect(Collectors.toList());
            node.set(entry.getKey(), createGposHorasJson(gpos));
            //array.add(node);
        }
//        for (DiaHoraGrupo hdiaGpo : diasHorasGpos) {
//            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
//            ObjectNode nodeGpo = JsonHelper.createJson(hdiaGpo.getGrupoHorario(), JsonNodeFactory.instance, new String[]{"*"});
//            node.set(hdiaGpo.getKey(), nodeGpo);
////            ObjectNode node = JsonHelper.createJson(hdiaGpo, JsonNodeFactory.instance, true, new String[]{
////                "*", "grupoHorario.codigo", "hora.id", "hora.codigo", "dia.id"
////            });
//            array.add(node);
//        }
        return node;
    }

}
