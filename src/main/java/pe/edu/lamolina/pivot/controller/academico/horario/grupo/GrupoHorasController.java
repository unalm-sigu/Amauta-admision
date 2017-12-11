package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.LetraGrupoHoraEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCicloEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoGrupoHorariosEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionGrupoEnum;
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

        TipoGrupoHoras tipoGrupoHoras = service.findTipoGrupoHoras(idTipoGrupo);
        model.addAttribute("tipoGrupoHoras", tipoGrupoHoras);
        return "academico/horario/grupo/grupo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, Long idTipoGrupo, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<GrupoHoras> grupos = service.allGrupoHoras(filter, idTipoGrupo);
            List<DiaHoraGrupo> horas = service.allDiaHoraGrupo(grupos);
            Map<Long, List<DiaHoraGrupo>> mapGrupohoras = TypesUtil.convertListToMapList("grupoHorario.id", horas);
            for (GrupoHoras grupo : grupos) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupo.getId());
                node.put("codigo", grupo.getCodigo());
                node.put("letra", grupo.getLetra());
                node.put("tipoCiclo", grupo.getTipoCiclo());
                node.put("tipoGrupoHoras", grupo.getTipoGrupoHoras() != null ? grupo.getTipoGrupoHoras().getCodigo() : "");
                node.put("tipoSeccion", grupo.getTipoSeccion());
                node.put("color", grupo.getColor());
                List<DiaHoraGrupo> mapGrupohora = mapGrupohoras.get(grupo.getId());
                node.put("estado", "default");
                node.put("horas", 0);
                if (mapGrupohora != null) {
                    node.put("horas", mapGrupohora.size());
                }
                if (TipoGrupoHorariosEnum.FLX.name().equalsIgnoreCase(grupo.getConHorario())) {
                    node.put("estado", "primary");
                } else {
                    if (mapGrupohora == null) {
                        node.put("estado", "danger");
                    } else {
                        if (mapGrupohora.size()>0) {
                            node.put("estado", "primary");
                        } else {
                            node.put("estado", "danger");
                        }
                    }
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
    public JsonResponse save(GrupoHoras grupoHoras, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            GrupoHoras grupoCode = service.findGrupoHorasByCode(grupoHoras.getCodigo());
            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
            if (grupoHoras.getId() != null) {
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
            response.setSuccess(Boolean.TRUE);
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
    public JsonResponse delete(GrupoHoras grupoHoras) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(grupoHoras);
            response.setMessage("Grupo Horas eliminada satisfactoriamente");
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
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            GrupoHoras grupoDb = service.findGrupoHoras(grupoHoras);
            TipoGrupoHoras tipoGrupoDb = grupoDb.getTipoGrupoHoras();
            List<DiaHoraGrupo> diaHoraGrupos = new ArrayList();
            if (TipoGrupoHorasEnum.ESPECIAL.name().equalsIgnoreCase(tipoGrupoDb.getTipo())) {
                diaHoraGrupos = service.allDiaHoraGrupoByGrupo(grupoDb, cicloAcademico);
            } else {
                diaHoraGrupos = service.allDiaHoraGrupoByTipo(grupoDb.getTipoGrupoHoras(), cicloAcademico);
            }
            Map<String, DiaHoraGrupo> mapDiaHoraGrupo = new LinkedHashMap<>();

            for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
                Long dia = diaHoraGrupo.getDia().getId();
                Long hora = diaHoraGrupo.getHora().getId();
                mapDiaHoraGrupo.put("" + dia + "_" + hora, diaHoraGrupo);
            }

            List<Dia> dias = service.allDia();
            List<Hora> horas = service.allHora();

            for (Hora hora : horas) {
                hora.setDiaHoraGrupo(null);
                List<DiaHoraGrupo> myDiaHoraGrupos = new ArrayList();
                for (Dia dia : dias) {
                    DiaHoraGrupo myDiaHoraGrupo = mapDiaHoraGrupo.get("" + dia.getId() + "_" + hora.getId());
                    if (myDiaHoraGrupo == null) {
                        myDiaHoraGrupo = new DiaHoraGrupo();
                        myDiaHoraGrupo.setDia(dia);
                        myDiaHoraGrupo.setHora(hora);
                        GrupoHoras gh = new GrupoHoras();
                        gh.setColor("#ffffff");
                        myDiaHoraGrupo.setGrupoHorario(gh);
                        myDiaHoraGrupos.add(myDiaHoraGrupo);
                    } else {
                        GrupoHoras ghoras = myDiaHoraGrupo.getGrupoHorario();
                        if (ghoras.getId() != grupoDb.getId().longValue()) {
                            ghoras.setColor(null);
                            myDiaHoraGrupo.setGrupoHorario(ghoras);
                        }
                        myDiaHoraGrupos.add(myDiaHoraGrupo);
                    }
                }
                hora.setDiaHoraGrupo(myDiaHoraGrupos);
            }

            Context ctx = new Context();
            ctx.setVariable("dias", dias);
            ctx.setVariable("horas", horas);
            ctx.setVariable("diaHoraGrupos", diaHoraGrupos);

            String htmlContent = springHtml.process("academico/horario/grupo/horarioTemplate", ctx);
            response.setData(htmlContent);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("asignarHora")
    public JsonResponse asignarHora(DiaHoraGrupo diaHoraGrupo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            diaHoraGrupo.setCicloAcademico(cicloAcademico);
            service.saveDiaHoraGrupo(diaHoraGrupo);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("desasignarHora")
    public JsonResponse desasignarHora(DiaHoraGrupo diaHoraGrupo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            diaHoraGrupo.setCicloAcademico(cicloAcademico);
            service.desasignarHora(diaHoraGrupo);
            response.setSuccess(Boolean.TRUE);
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

}
