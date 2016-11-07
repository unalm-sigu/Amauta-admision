package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@SessionAttributes("planCalificacion")
@RequestMapping("academico/systemcalifica/sistema")
public class SistemaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SistemaService sistemaService;

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
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/systemcalifica/sistema/sistema";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<PlanCalificacion> lstPLanCalificacion = sistemaService.allPlanesCalificacionByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PlanCalificacion planCalificacion : lstPLanCalificacion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", planCalificacion.getId());
                node.put("codigo", planCalificacion.getSistemaNotas().getCodigo());
                node.put("formula", "EP(20) EF(25) 5PC(40) 3TA(15)");
                node.put("origen", planCalificacion.getDepartamentoAcademico().getCodigo());
                node.put("estado", planCalificacion.getEstado());
                node.put("estadoEnum", planCalificacion.getEstadoEnum().getValue());
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

    @RequestMapping("{sistema}/detalleSistema")
    public String detalleSistema(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        PlanCalificacion planCalificacion = sistemaService.findPlanCalificacion(idSistema);
        model.addAttribute("planCalificacion", planCalificacion);
        return "app/academico/systemcalifica/sistema/detalleSistema";
    }

    @RequestMapping("{sistema}/cursos")
    public String cursos(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        PlanCalificacion planCalificacion = sistemaService.findPlanCalificacion(idSistema);
        model.addAttribute("planCalificacion", planCalificacion);
        return "app/academico/systemcalifica/sistema/cursos";
    }

    @RequestMapping("{sistema}/detalleSolicitud")
    public String detalleSolicitud(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/systemcalifica/sistema/detalleSolicitud";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        PlanCalificacion planCalificacion = new PlanCalificacion();

        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("tipoEvaluaciones", sistemaService.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", sistemaService.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEnum.values());
        return "app/academico/systemcalifica/sistema/nuevoSistema";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@ModelAttribute("planCalificacion") PlanCalificacion planCalificacion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

            logger.debug("Plan Califica {}", planCalificacion.toString());
            logger.debug("Planes de evaluacion {}", planCalificacion.getEvaluacionPlan().size());

            String message = "";
            if (planCalificacion.getId() == null) {
                planCalificacion.setDepartamentoAcademico(new DepartamentoAcademico(1));
                sistemaService.saveSistemaCalifica(planCalificacion);
                message = "Creado exitosamente.";

            } else {
                message = "Actualizado exitosamente.";
            }
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage(message);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("aprobar")
    public JsonResponse aprobar(@RequestParam("sistema") Long sistema) {
        JsonResponse response = new JsonResponse();
        try {
            sistemaService.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.APR);
            response.setMessage(Messages.APPROVED);
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

    @ResponseBody
    @RequestMapping("desaprobar")
    public JsonResponse desaprobar(@RequestParam("sistema") Long sistema) {
        JsonResponse response = new JsonResponse();
        try {
            sistemaService.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.DES);
            response.setMessage(Messages.DISAPPROVE);
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

    @ResponseBody
    @RequestMapping("anull")
    public JsonResponse anull(@RequestParam("sistema") Long sistema) {
        JsonResponse response = new JsonResponse();
        try {
            sistemaService.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.ANU);
            response.setMessage(Messages.ANNULL);
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

    @ResponseBody
    @RequestMapping("tiposEvaluacion")
    public JsonResponse tiposEvaluacion() {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<TipoEvaluacion> lstTipoEvaluacion = sistemaService.allTipoEvaluacion();

            ObjectNode json = new ObjectNode(jsonFactory);

            for (TipoEvaluacion tipoEvaluacion : lstTipoEvaluacion) {
                json.put(tipoEvaluacion.getId().toString(), tipoEvaluacion.getCodigo());
            }

            response.setSuccess(true);
            response.setData(json);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
