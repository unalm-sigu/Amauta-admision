package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

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
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.OrigenPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@SessionAttributes("dntEvaluacionPlan")
@RequestMapping("academico/docente/cargaacademica")
public class CargaAcademicaController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CargaAcademicaService cargaAcademicaService;
    
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
        model.addAttribute("docente", ds.getDocente());
        cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente());
        return "app/academico/docente/cargaacademica/cargaAcademica";
    }
    
    @RequestMapping("sistemaCurso")
    public String sistemaCurso(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        return "app/academico/docente/cargaacademica/sistemaCurso";
    }
    
    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        
        DynatableResponse json = new DynatableResponse();
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        try {
            
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            
            List<DocenteSeccion> lista = cargaAcademicaService.allByCargaAcademica(filter, ds.getDocente());
            logger.debug("Lista {}", lista.size());
            for (DocenteSeccion docSeccion : lista) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                
                node.put("id", docSeccion.getSeccion().getId());
                node.put("idCurso", docSeccion.getSeccion().getGrupoSeccion().getCurso().getId());
                node.put("idSistemaCalificacion", docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion() != null
                        ? docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion().getId().toString() : "");
                node.put("sistemaCalificacion", docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion() != null ? docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion().getCodigo() : "");
                node.put("nombre", docSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());
                node.put("codigo", docSeccion.getSeccion().getGrupoSeccion().getCurso().getCodigo());
                node.put("tpc", docSeccion.getSeccion().getGrupoSeccion().getCurso().getTpc());
                node.put("seccion", docSeccion.getSeccion().getCodigo());
                node.put("aula", docSeccion.getSeccion().getAula().getNombre());
                node.put("tipoSeccion", docSeccion.getSeccion().getTipoSeccion());
                node.put("alumnos", 35);
                node.put("horasSemanales", 3);
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion() != null
                        ? docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion().getEstado() : "");
                node.put("estadoSistemaEnum", docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion() != null
                        ? docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion().getEstadoEnum().getValue() : "");
                array.add(node);
                /*
                Long idPlanCalificacion = docSeccion.getSeccion().getGrupoSeccion().getCurso().getPlanCalificacion().getId();
                Long idGrupoSeccion = docSeccion.getSeccion().getGrupoSeccion().getId();
                EvaluacionSeccion evalPlan = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
                node.put("idEvalSeccion", (evalPlan != null ? evalPlan.getId().toString() : "0"));
                 */
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
    @RequestMapping("listEvaluacionPlan")
    public DynatableResponse listEvaluacionPlan(@ModelAttribute("dntEvaluacionPlan") List dntEvaluacionPlan,
            DynatableFilter filter,
            @RequestParam("evaluacionSeccion") Long evaluacionSeccionId,
            HttpSession session) {
        logger.debug("evaluacion seccion id {}", evaluacionSeccionId);
        DynatableResponse json = new DynatableResponse();
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvaluacionSeccion(evaluacionSeccionId);
        logger.debug("la evaluacion seccion {}", evaluacionSeccion.getId());
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            
            List<Evaluacion> lstEvaluacionPlan = cargaAcademicaService.allEvaluacionesByEvalSeccion(new EvaluacionSeccion(evaluacionSeccionId));
            //  List<Evaluacion> lstEvaluacionPlan = dntEvaluacionPlan;
            logger.debug("Lista {}", lstEvaluacionPlan.size());
            
            for (Evaluacion evaluacionPlan : lstEvaluacionPlan) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("evaPlanId", evaluacionPlan.getId());
                node.put("tipoEvalCod", evaluacionPlan.getTipoEvaluacion().getCodigo());
                node.put("tipoEvalNombre", evaluacionPlan.getTipoEvaluacion().getNombre());
                node.put("cantEvaluaciones", "0");
                node.put("pesoEvaluacion", evaluacionPlan.getPeso());
                node.put("esHijo", false);
                array.add(node);
                
                for (Evaluacion evaluacionHija : evaluacionPlan.getEvaluaciones()) {
                    ObjectNode nodeHijo = new ObjectNode(JsonNodeFactory.instance);
                    
                    logger.debug("Tipo evaluacion {}", evaluacionHija.getTipoEvaluacion().getNombre());
                    nodeHijo.put("evaPlanId", evaluacionHija.getId());
                    nodeHijo.put("tipoEvalCod", evaluacionHija.getTipoEvaluacion().getCodigo());
                    nodeHijo.put("tipoEvalNombre", evaluacionHija.getTipoEvaluacion().getNombre());
                    nodeHijo.put("cantEvaluaciones", "0");
                    nodeHijo.put("pesoEvaluacion", evaluacionHija.getPeso());
                    nodeHijo.put("esHijo", true);
                    array.add(nodeHijo);
                }
                
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
    
    @RequestMapping("{seccion}/detalleSistemaCalificacion")
    public String detalleSistemaCalificacion(@PathVariable("seccion") Long idSeccion, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        
        return "app/academico/docente/cargaacademica/detalleSistemaCalificacion";
    }
    
    @RequestMapping("expandir/{seccion}")
    public String expandir(Model model, HttpSession session, @PathVariable("seccion") Long idSeccion) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        
        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");
            
        }
        
        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));
        
        Long idPlanCalificacion = seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId();
        Long idGrupoSeccion = seccion.getGrupoSeccion().getId();
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
        model.addAttribute("evaluacionSeccion", evalSeccion);
        
        cargaAcademicaService.createEvaluacionPorEvalSeccion(evalSeccion);
        /*
        List<Evaluacion> evaluaciones = cargaAcademicaService.allEvaluacionesByEvalSeccion(evalSeccion);

        model.addAttribute("dntEvaluacionPlan", evaluaciones);*/
        return "app/academico/docente/cargaacademica/expandirSistemaCalificacion";
    }
    
    @RequestMapping("nuevo/{seccion}")
    public String nuevo(Model model, HttpSession session, @PathVariable("seccion") Long idSeccion) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        
        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");
            
        }
        
        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getCurso().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));
        
        Long idPlanCalificacion = seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId();
        Long idGrupoSeccion = seccion.getGrupoSeccion().getId();
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
        model.addAttribute("evaluacionSeccion", evalSeccion);
        
        PlanCalificacion planCalificacion = new PlanCalificacion();
        
        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("tipoEvaluaciones", cargaAcademicaService.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", cargaAcademicaService.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEnum.values());
        
        return "app/academico/docente/cargaacademica/nuevoSistemaCalificacion";
    }
    
    @ResponseBody
    @RequestMapping("saveExpandir")
    public JsonResponse saveExpandir(Model model,
            @ModelAttribute("evaluacion") Evaluacion evaluacion,
            @PathVariable("seccion") Long idSeccion,
            RedirectAttributes redirectAttr, HttpSession session) {
        
        JsonResponse response = new JsonResponse();
        try {
            DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
            
            cargaAcademicaService.saveExpansionEvaluacion(evaluacion, ds);
            logger.debug("La evaluacion seccion es {}", evaluacion.getEvaluacionSeccion().getId());
            /*
            List<Evaluacion> evaluaciones = cargaAcademicaService.allEvaluacionesByEvalSeccion(evaluacion.getEvaluacionSeccion());
            model.addAttribute("dntEvaluacionPlan", evaluaciones);
            session.setAttribute("dntEvaluacionPlan", evaluaciones);*/
            
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage("Evaluacion Expandida");
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
    @RequestMapping("saveSistema")
    public JsonResponse saveSistema(@ModelAttribute("planCalificacion") PlanCalificacion planCalificacion,
            RedirectAttributes redirectAttr, HttpSession session) {
        
        JsonResponse response = new JsonResponse();
        try {
            DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
            
            String message = "";
            if (planCalificacion.getId() == null) {
                planCalificacion.setDepartamentoAcademico(ds.getDepartamentoAcademico());
                planCalificacion.setOrigenEnum(OrigenPlanCalificaEnum.DOC);
                cargaAcademicaService.saveSistemaCalifica(planCalificacion);
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
    
    @RequestMapping("detalleExpandirEvaluacion")
    public String detalleExapandirEva(Model model, HttpSession session,
            @RequestParam(value = "evaluacion", required = false) Long evaluacionId) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);
        List<TipoEvaluacion> lstTipoEvas = cargaAcademicaService.allTipoEvaluacion();
        List<TipoEvaluacion> lstTipoEvasReal = new ArrayList<>();
        for (TipoEvaluacion tEval : lstTipoEvas) {
            boolean found = false;
            for (Evaluacion eva : evaluacion.getEvaluaciones()) {
                if (eva.getTipoEvaluacion().getId().equals(tEval.getId())) {
                    found = true;
                }
            }
            if (!found) {
                lstTipoEvasReal.add(tEval);
            }
        }
        model.addAttribute("tipoEvaluaciones", lstTipoEvasReal);
        model.addAttribute("evaluacion", evaluacion);
        return "app/academico/docente/cargaacademica/detalleExpandirEvaluacion";
    }
    
    @RequestMapping("{cargaAcademica}/notasAcademicas")
    public String notasAcademicas(@PathVariable("cargaAcademica") Long idCargaAcademica, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        return "app/academico/docente/cargaacademica/notasAcademicas";
    }
    
    @RequestMapping("{evaluacion}/evaluacion")
    public String evaluacion(@PathVariable("evaluacion") Long idEvaluacion, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        Evaluacion eval = new Evaluacion();
        eval.setTipoEvaluacion(new TipoEvaluacion());
        eval.getTipoEvaluacion().setCodigo("PC1");
        model.addAttribute("evaluacion", eval);
        
        return "app/academico/docente/cargaacademica/notasAcademicas";
    }
    
    @RequestMapping("detalleCambioNota")
    public String detalleCambioNota(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        return "app/academico/docente/cargaacademica/detalleCambioNota";
    }
    
    @RequestMapping("unalm")
    public String unalm() {
        
        return "app/unalm/unalm";
    }
    
    @RequestMapping("detalleNotasAcademicas")
    public String detalleNotasAcademicas(Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/detalleNotasAcademicas";
    }
}
