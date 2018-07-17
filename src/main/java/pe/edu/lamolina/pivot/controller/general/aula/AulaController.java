package pe.edu.lamolina.pivot.controller.general.aula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static javax.management.Query.attr;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoAmbienteEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/aula")
public class AulaController {

    @Autowired
    AulaService service;

    @Autowired
    SpringTemplateEngine springHtml;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        return "general/aula/aula";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatableee(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Aula> aulas = service.allByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Aula aula : aulas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", aula.getId());
                node.put("codigo", aula.getCodigo());
                node.put("nombre", aula.getNombre());
                node.put("tipoAmbienteEnum", aula.getTipoAmbienteEnum().getValue());
                node.put("tipoAmbiente", aula.getTipoAmbiente());
                node.put("piso", aula.getPiso());
                node.put("pisos", aula.getPisos());
                node.put("aforo", aula.getAforo());
                node.put("pabellon", (String) ObjectUtil.getParentTree(aula, "aulaSuperior.nombre"));
                node.put("capacidad", aula.getCapacidadAula());
                node.put("sede", aula.getSede() != null ? aula.getSede().getNombre() : "");
                node.put("tipoAula", aula.getTipoAula() != null ? aula.getTipoAula().getNombre() : "");
                node.put("gestor", aula.getOficinaSupervisora() != null ? aula.getOficinaSupervisora().getNombre() : "");
                node.put("estado", aula.getEstado());
                node.put("estadoEnum", aula.getEstadoEnum().getValue());
                node.put("motivo", aula.getMotivoAnulacion());
                node.put("aulasContenido", aula.getAulasContenido().size());

                ArrayNode arrayHijas = new ArrayNode(JsonNodeFactory.instance);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = new ObjectNode(JsonNodeFactory.instance);
                    nodeHija.put("codigo", aulaHija.getCodigo());
                    nodeHija.put("nombre", aulaHija.getNombre());
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);

                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }
        return json;
    }

//    @ResponseBody
//    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Aula> aulas = service.allByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Aula aula : aulas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", aula.getId());
                node.put("codigo", aula.getCodigo());
                node.put("nombre", aula.getNombre());
                node.put("tipoAmbienteEnum", aula.getTipoAmbienteEnum().getValue());
                node.put("piso", aula.getPiso());
                node.put("pisos", aula.getPisos());
                node.put("aforo", aula.getAforo());
                node.put("pabellon", (String) ObjectUtil.getParentTree(aula, "aulaSuperior.nombre"));
                node.put("capacidad", aula.getCapacidadAula());
                node.put("tipoAmbiente", aula.getTipoAmbiente());
                node.put("aulasContenido", aula.getAulasContenido().size());
                ObjectNode objSede = JsonHelper.createJson(aula.getSede(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("sede", objSede);
                ObjectNode objTipoAula = JsonHelper.createJson(aula.getTipoAula(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("tipoAula", objTipoAula);
                ObjectNode objOficina = JsonHelper.createJson(aula.getOficinaSupervisora(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("gestor", objOficina);
                node.put("estado", aula.getEstado());
                node.put("estadoEnum", aula.getEstadoEnum().getValue());
                node.put("motivo", aula.getMotivoAnulacion());

                ArrayNode arrayHijas = new ArrayNode(JsonNodeFactory.instance);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = new ObjectNode(JsonNodeFactory.instance);
                    nodeHija.put("codigo", aulaHija.getCodigo());
                    nodeHija.put("nombre", aulaHija.getNombre());
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);
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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Aula aula = new Aula();

        model.addAttribute("aula", aula);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        model.addAttribute("tiposAula", service.allTiposAula());
        model.addAttribute("sedes", service.allSedes());
        return "general/aula/aulaForm";
    }

    @RequestMapping("save")
    public String save(Aula aula, RedirectAttributes redirectAttr, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = aula.getId() != null ? Messages.UPDATED : Messages.CREATED;
            if (aula.getId() == null) {
                service.save(aula, ds.getUsuario());
            } else {
                service.update(aula, ds.getUsuario());
            }
            Notificaciones.crearMsg(mensaje, redirectAttr);

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);

        }
        return "redirect:/general/aula";
    }

    @RequestMapping("editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Aula aula = service.findAulaById(id);

        model.addAttribute("aula", aula);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        model.addAttribute("tiposAula", service.allTiposAula());
        model.addAttribute("sedes", service.allSedes());
        return "general/aula/aulaForm";
    }

    @ResponseBody
    @RequestMapping("allAulasSuperiores")
    public JsonResponse allAulasSuperiores(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Aula> aulasSuperiores = service.allAulasSuperioresByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Aula aula : aulasSuperiores) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", aula.getId());
                json.put("nombre", aula.getNombre());

                jsonList.add(json);

            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allGestores")
    public JsonResponse allGestores(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Oficina> gestores = service.allOficinasByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Oficina gestor : gestores) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", gestor.getId());
                json.put("nombre", gestor.getNombre());
                json.put("codigo", gestor.getCodigo());

                jsonList.add(json);

            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambioEstado")
    public JsonResponse cambioEstadoOrientacionCarrera(Aula aula, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambioEstado(aula, ds);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(Aula aula, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.eliminarAula(aula, ds);

            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    //
    @ResponseBody
    @RequestMapping("loadModalAulaHorario")
    public JsonResponse loadModalAulaHorario(
            @RequestParam("aula") Long aulaId, HttpSession session, Model model) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Aula aula = service.findAulaFull(aulaId, ds.getCicloAcademico());

            List<Dia> dias = service.allDia();
            List<HorarioAula> horarioAula = aula.getHorariosAula();
            Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("hora.id", "hora", horarioAula);
            HelperHorarioAula helper = new HelperHorarioAula();
            List<Hora> horas = new ArrayList(mapHoras.values());
            Collections.sort(horas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));

            Context ctx = new Context();
            ctx.setVariable("horas", horas);
            ctx.setVariable("dias", dias);
            ctx.setVariable("horario", horarioAula);
            ctx.setVariable("helper", helper);

            String htmlContent = springHtml.process("general/aula/aulaHorarioTemplate", ctx);
            response.setData(htmlContent);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
