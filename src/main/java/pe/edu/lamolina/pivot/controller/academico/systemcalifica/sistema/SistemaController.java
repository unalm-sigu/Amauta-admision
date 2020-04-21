package pe.edu.lamolina.pivot.controller.academico.systemcalifica.sistema;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEvalEnum;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
//@SessionAttributes("planCalificacion")
@RequestMapping("academico/systemcalifica/sistema")
public class SistemaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SistemaService service;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("departamentos", ds.getDepartamentos());
        return "academico/systemcalifica/sistema/sistema";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<PlanCalificacion> lstPLanCalificacion = service.allPlanesCalificacionByDynatable(filter, ds);
            logger.debug("Tamaño lista plan {}", lstPLanCalificacion.size());
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PlanCalificacion planCalificacion : lstPLanCalificacion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", planCalificacion.getId());
                node.put("codigo", planCalificacion.getCodigo());
                node.put("formula", planCalificacion.getFormula());
                node.put("tipoCiclo", planCalificacion.getTipoCicloEnum().getValue());
                node.put("descripcion", planCalificacion.getDescripcion());
                node.put("origen", planCalificacion.getOrigenEnum().getValue());
                node.put("fechaReg", TypesUtil.getStringDate(planCalificacion.getFechaRegistro(), "dd/MM/yyyy"));
                node.put("estado", planCalificacion.getEstado());
                node.put("estadoEnum", planCalificacion.getEstadoEnum().getValue());
                node.put("verSolicitud", planCalificacion.isEstadoSolicitado());
                node.put("verActivar", planCalificacion.isEstadoCreado());
                node.put("verInactivar", planCalificacion.isEstadoCreado() || planCalificacion.isEstadoActivado());
                node.put("verAprobar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());

                node.put("verRechazar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());
                node.put("verObservar", planCalificacion.isEstadoSolicitado() || planCalificacion.isEstadoReenviado());
                node.put("verReenviar", planCalificacion.isEstadoObservado());
                node.put("verAsignarCursos", planCalificacion.isEstadoActivado());

                List<Curso> cursos = new ArrayList<>();
                if (planCalificacion.isTipoCicloNivelacion()) {
                    if (ObjectUtil.getParentTree(planCalificacion, "curso") != null) {
                        for (Curso cur : planCalificacion.getCurso()) {
                            if (cur.isEstadoActive()) {
                                cursos.add(cur);
                            }
                        }
                    }
                } else if (planCalificacion.isTipoCicloRegular()) {
                    if (ObjectUtil.getParentTree(planCalificacion, "cursosPlanRegular") != null) {
                        for (Curso cur : planCalificacion.getCursosPlanRegular()) {
                            if (cur.isEstadoActive()) {
                                cursos.add(cur);
                            }
                        }
                    }
                }
                //   node.put("cantidadCursos", cursos.size());
                node.put("cantidadCursos", planCalificacion.getPlanCalificacionCursos().size());
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
    @RequestMapping("listCursos")
    public DynatableResponse listCursos(DynatableFilter filter, @RequestParam("planCalificacion") Long planCalificacion, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            logger.debug("listCursos Plancalificacion {}", planCalificacion);
            CicloAcademico ciclo = ds.getCicloAcademico();
            /*
                List<Curso> cursos = service.allCursosByPlanCalifica(filter, planCalificacion, ds.getDepartamentoAcademico().getId());
                        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Curso curso : cursos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", curso.getId());
                node.put("codigo", curso.getCodigo());
                node.put("nombre", curso.getNombre());
                node.put("fechaInclusion", curso.getFechaPlanCalificacion() != null ? TypesUtil.getStringDate(curso.getFechaPlanCalificacion(), "dd/MM/yyyy") : "");
                node.put("tpc", curso.getTpc());
                array.add(node);
            }
             */
            List<PlanCalificacionCurso> planCursos = service.allPlanCalificacionCursosByFilterDyna(filter, new PlanCalificacion(planCalificacion));

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PlanCalificacionCurso planCurso : planCursos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", planCurso.getId());
                node.put("codigo", planCurso.getCurso().getCodigo());
                node.put("nombre", planCurso.getCurso().getNombre());
                node.put("fechaInclusion", planCurso.getFechaCreacion() != null ? TypesUtil.getStringDate(planCurso.getFechaCreacion(), "dd/MM/yyyy") : "");
                node.put("tpc", planCurso.getCurso().getTpc());
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        PlanCalificacion planCalificacion = service.findPlanCalificacion(idSistema);
        /*    List<Curso> cursosByPlan = new ArrayList<>();
        for (PlanCalificacionCurso planCurso : planCalificacion.getPlanCalificacionCursos()) {
            cursosByPlan.add(planCurso.getCurso());
        }
         */
        model.addAttribute("planCalificacion", planCalificacion);
        //  model.addAttribute("cursosByPlan", cursosByPlan);
        model.addAttribute("tieneCursos", (!planCalificacion.getPlanCalificacionCursos().isEmpty()));
        return "academico/systemcalifica/sistema/detalleSistema";
    }

    @RequestMapping("{sistema}/cursos")
    public String cursos(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        PlanCalificacion planCalificacion = service.findPlanCalificacion(idSistema);
        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("departamento", planCalificacion.getDepartamentoAcademico());

        return "academico/systemcalifica/sistema/cursos";
    }

    @RequestMapping("{sistema}/detalleSolicitud")
    public String detalleSolicitud(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        logger.debug("El sistema califica {}", idSistema);
        PlanCalificacion planCalificacion = service.findPlanCalificacion(idSistema);
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        model.addAttribute("planCalificacion", planCalificacion);

        return "academico/systemcalifica/sistema/detalleSolicitud";
    }

    @RequestMapping("nuevo/{departamento}")
    public String nuevo(@PathVariable("departamento") Long idDepartamento, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        DepartamentoAcademico departamento = service.buscarDepartamento(idDepartamento, ds);
        if (departamento == null) {
            return "redirect:/academico/systemcalifica/sistema";
        }

        model.addAttribute("tipoEvaluaciones", service.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", service.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEvalEnum.values());
        model.addAttribute("departamento", departamento);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        return "academico/systemcalifica/sistema/nuevoSistema";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(PlanCalificacion planCalificacion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            String message = "";
            if (planCalificacion.getId() == null) {

                service.saveSistemaCalifica(planCalificacion, ds);
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
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("aprobar")
    public JsonResponse aprobar(@RequestParam("sistema") Long sistema,
            @RequestParam(value = "comentario", required = false) String comentario,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            logger.debug("el comentario es {}", comentario);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.changeStatePlanCalificacion(sistema, comentario, EstadoPlanCalificaEnum.ACEP, ds.getUsuario());
            response.setMessage(GlobalMessages.APPROVED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("rechazar")
    public JsonResponse rechazar(@RequestParam("sistema") Long sistema,
            @RequestParam(value = "comentario", required = false) String comentario,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            logger.debug("el comentario es {}", comentario);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.changeStatePlanCalificacion(sistema, comentario, EstadoPlanCalificaEnum.RHZ, ds.getUsuario());
            response.setMessage(GlobalMessages.REJECT);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("observar")
    public JsonResponse observar(@RequestParam("sistema") Long sistema,
            @RequestParam(value = "comentario", required = false) String comentario,
            HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.changeStatePlanCalificacion(sistema, comentario, EstadoPlanCalificaEnum.OBS, ds.getUsuario());
            response.setMessage(GlobalMessages.OBSERVED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("activar")
    public JsonResponse activar(@RequestParam("sistema") Long sistema, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.ACT, ds.getUsuario());
            response.setMessage(GlobalMessages.ACTIVATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("inactivar")
    public JsonResponse inactivar(@RequestParam("sistema") Long sistema, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.INA, ds.getUsuario());
            response.setMessage(GlobalMessages.INACTIVATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("anull")
    public JsonResponse anull(@RequestParam("sistema") Long sistema, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.changeStatePlanCalificacion(sistema, EstadoPlanCalificaEnum.INA, ds.getUsuario());
            response.setMessage(GlobalMessages.ANNULL);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
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
            ObjectNode json = service.allTipoEvaluacionJson();

            response.setSuccess(true);
            response.setData(json);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("incluirCurso")
    public JsonResponse incluirCurso(
            @RequestParam("curso") Long curso,
            @RequestParam("planCalificacion") Long planCalificacion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.asignarCurso(curso, planCalificacion, ds);
            response.setMessage("Curso asignado.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("desasignarCurso")
    public JsonResponse desasignarCurso(@RequestParam("planCurso") Long planCurso, HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.desasignarCurso(planCurso, ds.getPersona().getId());
            response.setMessage("Curso desasignado.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
