package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.amazonaws.util.json.JSONObject;
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
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.OrigenPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("docente", ds.getDocente());
        logger.debug("el docente logeado es {}", ds.getDocente().getId());
        cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "app/academico/docente/cargaacademica/cargaAcademica";
    }

    @RequestMapping("sistemaCurso")
    public String sistemaCurso(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/sistemaCurso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<DocenteSeccion> lista = cargaAcademicaService.allByCargaAcademica(filter, ds.getDocente());
            logger.debug("Lista {}", lista.size());
            for (DocenteSeccion docSeccion : lista) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                GrupoSeccion grupoSeccion = docSeccion.getSeccion().getGrupoSeccion();
                PlanCalificacion planCalificacion = grupoSeccion.getPlanCalificacion();

                node.put("id", docSeccion.getSeccion().getId());
                node.put("docenteSeccion", docSeccion.getId());
                node.put("idCurso", docSeccion.getSeccion().getGrupoSeccion().getCurso().getId());
                node.put("idSistemaCalificacion", planCalificacion != null ? planCalificacion.getId().toString() : "");
                node.put("sistemaCalificacion", planCalificacion != null ? planCalificacion.getCodigo() : "");
                node.put("nombre", docSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());
                node.put("codigo", docSeccion.getSeccion().getGrupoSeccion().getCurso().getCodigo());
                node.put("tpc", docSeccion.getSeccion().getGrupoSeccion().getCurso().getTpc());
                node.put("seccion", docSeccion.getSeccion().getCodigo());
                node.put("idSeccion", docSeccion.getSeccion().getId());
                node.put("aula", docSeccion.getSeccion().getAula().getNombre());
                node.put("tipoSeccion", docSeccion.getSeccion().getTipoSeccion());
                node.put("alumnos", docSeccion.getSeccion().getMatriculados());
                node.put("horasSemanales", docSeccion.getSeccion().getHorasSemanales());
                node.put("estado", "DIC");
                node.put("estadoEnum", "Dictando");
                node.put("estadoSistema", docSeccion.getSeccion().getGrupoSeccion() != null
                        ? docSeccion.getSeccion().getGrupoSeccion().getEstadoPlan() : "");
                String estadoEnum = "";
                if (grupoSeccion != null
                        && grupoSeccion.getEstadoPlanEnum() != null) {
                    estadoEnum = grupoSeccion.getEstadoPlanEnum().getValue();
                }
                node.put("estadoSistemaEnum", estadoEnum);

                node.put("verDetalleSistemaCal", false);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoSolicitado()
                            || grupoSeccion.isEstadoExpandido()
                            || grupoSeccion.isEstadoExpandir()) {
                        node.put("verDetalleSistemaCal", true);
                    }
                }
                node.put("verAceptarSistemaCal", false);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoPropuesto()) {
                        node.put("verAceptarSistemaCal", true);
                    }
                }
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
    public DynatableResponse listEvaluacionPlan(
            DynatableFilter filter,
            @RequestParam("evaluacionSeccion") Long evaluacionSeccionId,
            HttpSession session) {
        logger.debug("evaluacion seccion id {}", evaluacionSeccionId);
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        logger.debug("la evaluacion seccion {}", evaluacionSeccionId);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<EvaluacionExpandida> lstEvaluacionPlan = cargaAcademicaService.allEvaluacionesExpByEvalSeccion(new EvaluacionSeccion(evaluacionSeccionId));
            //  List<Evaluacion> lstEvaluacionPlan = dntEvaluacionPlan;
            logger.debug("Lista {}", lstEvaluacionPlan.size());

            for (EvaluacionExpandida evaluacionPlan : lstEvaluacionPlan) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("evaPlanId", evaluacionPlan.getId());
                node.put("tipoEvalCod", evaluacionPlan.getTipoEvaluacion().getCodigo());
                node.put("tipoEvalNombre", evaluacionPlan.getTipoEvaluacion().getNombre() + " " + evaluacionPlan.getNumero());
                node.put("numero", evaluacionPlan.getNumero());
                node.put("pesoEvaluacion", evaluacionPlan.getPeso());
                node.put("esHijo", false);
                node.put("desagregado", evaluacionPlan.isDesagregado());
                array.add(node);

                for (EvaluacionExpandida evaluacionHija : evaluacionPlan.getEvaluaciones()) {
                    ObjectNode nodeHijo = new ObjectNode(JsonNodeFactory.instance);

                    logger.debug("Tipo evaluacion {}", evaluacionHija.getTipoEvaluacion().getNombre() + " " + evaluacionPlan.getNumero());
                    nodeHijo.put("evaPlanId", evaluacionHija.getId());
                    nodeHijo.put("tipoEvalCod", evaluacionHija.getTipoEvaluacion().getCodigo());
                    nodeHijo.put("tipoEvalNombre", evaluacionHija.getTipoEvaluacion().getNombre());
                    nodeHijo.put("numero", evaluacionHija.getNumero());
                    nodeHijo.put("pesoEvaluacion", evaluacionHija.getPeso());
                    nodeHijo.put("esHijo", true);
                    nodeHijo.put("desagregado", evaluacionHija.isDesagregado());
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("planCalificacion", seccion.getGrupoSeccion().getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        logger.debug("La seccion es {}", seccion.getId());
        return "app/academico/docente/cargaacademica/detalleSistemaCalificacion";
    }

    @RequestMapping("expandir/{seccion}")
    public String expandir(Model model, HttpSession session, @PathVariable("seccion") Long idSeccion) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());

        logger.debug("El grupo seccion es {}", grupoSeccion.getId());
        logger.debug("La seccion es {}", seccion.getId());

        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");

        }

        model.addAttribute("planCalificacion", grupoSeccion.getPlanCalificacion());
        model.addAttribute("curso", seccion.getGrupoSeccion().getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));

        //   Long idPlanCalificacion = seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId();
        Long idGrupoSeccion = grupoSeccion.getId();
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, idGrupoSeccion);
        model.addAttribute("evaluacionSeccion", evalSeccion);
        logger.debug("la evaluacion seccion es {}", evalSeccion.getId());
        cargaAcademicaService.createEvaluacionExpPorEvalSeccion(evalSeccion, EstadoPlanCalificaEnum.EXPR);

        return "app/academico/docente/cargaacademica/expandirSistemaCalificacion";
    }

    @RequestMapping("nuevo/{seccion}")
    public String nuevo(Model model, HttpSession session, @PathVariable("seccion") Long idSeccion) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

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

        Long idPlanCalificacion = seccion.getGrupoSeccion().getPlanCalificacion().getId();
        Long idGrupoSeccion = seccion.getGrupoSeccion().getId();
        logger.debug("EL plan calificacion es {}, el grupo seccion es {}", idPlanCalificacion, idGrupoSeccion);
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(idPlanCalificacion, idGrupoSeccion);
        logger.debug("La evaluacion seccion es {}", evalSeccion != null ? evalSeccion.getId().toString() : "no se encontro");
        model.addAttribute("evaluacionSeccion", evalSeccion);

        PlanCalificacion planCalificacion = new PlanCalificacion();

        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("tipoEvaluaciones", cargaAcademicaService.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", cargaAcademicaService.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEvalEnum.values());

        return "app/academico/docente/cargaacademica/nuevoSistemaCalificacion";
    }

    @ResponseBody
    @RequestMapping("saveExpandir")
    public JsonResponse saveExpandir(Model model,
            @ModelAttribute EvaluacionExpandida evaluacion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            cargaAcademicaService.saveExpansionEvaluacion(evaluacion, ds);
            logger.debug("La evaluacion seccion es {}", evaluacion.getEvaluacionSeccion().getId());
            /*
            List<Evaluacion> evaluaciones = cargaAcademicaService.allEvaluacionesByEvalSeccion(evaluacion.getEvaluacionSeccion());
            model.addAttribute("dntEvaluacionPlan", evaluaciones);
            session.setAttribute("dntEvaluacionPlan", evaluaciones);
             */

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
    public JsonResponse saveSistema(@RequestParam("grupoSeccionId") Long grupoSeccionId,
            @ModelAttribute("planCalificacion") PlanCalificacion planCalificacion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Grupo Seccon Id {}", grupoSeccionId);
            String message = "";
            if (planCalificacion.getId() == null) {
                planCalificacion.setDepartamentoAcademico(ds.getDepartamentoAcademico());
                planCalificacion.setOrigenEnum(OrigenPlanCalificaEnum.DOC);
                cargaAcademicaService.saveSistemaCalifica(planCalificacion, grupoSeccionId);
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
    @RequestMapping("getSistemaNotas")
    public JsonResponse getSistemaNotas(@RequestParam("sistemaNotas") Long idSistemaNotas,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Sistema Notas {}", idSistemaNotas);

            SistemaNotas sistemaNotas = cargaAcademicaService.findSistemaNotaById(idSistemaNotas);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("esNumerico", sistemaNotas.isNumerico());
            node.put("valorInicial", sistemaNotas.getValorInicio());
            node.put("valorFinal", sistemaNotas.getValorFinal());
            node.put("minimoAprobatorio", sistemaNotas.getMinimoAprobatorio());
            node.put("letras", "");

            StringBuilder strbLetras = new StringBuilder();
            if (!sistemaNotas.isNumerico() && (sistemaNotas.getNotaLetra() != null && !sistemaNotas.getNotaLetra().isEmpty())) {
                for (NotaLetra notaLetra : sistemaNotas.getNotaLetra()) {

                    JSONObject jobj = new JSONObject();
                    jobj.put("esProbatoria", notaLetra.isAprobatorio());
                    jobj.put("valor", notaLetra.getValor());

                    node.put(notaLetra.getLetra(), jobj.toString());
                    strbLetras.append(notaLetra.getLetra()).append(",");
                }
                node.put("letras", strbLetras.substring(0, strbLetras.length() - 1));
            }

            response.setData(node);
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

    @RequestMapping("detalleExpandirEvaluacion")
    public String detalleExapandirEva(Model model, HttpSession session,
            @RequestParam(value = "evaluacion", required = false) Long evaluacionId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        EvaluacionExpandida evaluacion = cargaAcademicaService.findEvaluacionExpandida(evaluacionId);
        List<TipoEvaluacion> lstTipoEvas = cargaAcademicaService.allTipoEvaluacion();
        List<TipoEvaluacion> lstTipoEvasReal = new ArrayList<>();
        for (TipoEvaluacion tEval : lstTipoEvas) {
            boolean found = false;
            for (EvaluacionExpandida eva : evaluacion.getEvaluaciones()) {
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

    @RequestMapping("{docenteSeccion}/notasAcademicas")
    public String notasAcademicas(
            @PathVariable("docenteSeccion") Long idDocenteSeccion,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("el idDocenteSeccion {}", idDocenteSeccion);
        // logger.debug("el idCurso {}", idCurso);

        DocenteSeccion docenteSeccion = cargaAcademicaService.findDocenteSeccion(idDocenteSeccion);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(docenteSeccion.getSeccion().getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId());
        Seccion seccion = docenteSeccion.getSeccion();

        model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("sistemaNotas", evaluacionSeccion.getSistemaNotas());

        //List<Evaluacion> evaluacionesByGrupoSeccion = cargaAcademicaService.allEvaluacionByFilter(null, null, docenteSeccion.getSeccion().getId());
        List<Seccion> secciones = new ArrayList();
        secciones.add(seccion);
        if (seccion.getSeccionSuperior() != null) {
            secciones.add(seccion.getSeccionSuperior());
        }
        List<Evaluacion> evaluacionesBySeccion = cargaAcademicaService.allEvaluacionBySecciones(secciones);
        logger.debug("Grupo Seccion {}, Cantidad de Evaluaciones {}", docenteSeccion.getSeccion().getGrupoSeccion().getId(), evaluacionesBySeccion.size());
        //List<Evaluacion> evaluacionesByTipoSeccion = new ArrayList<>();
//        for (Evaluacion evaluacion : evaluacionesBySeccion) {
//            logger.debug("El tipo seccion del docente es {}, el tipo seccion de la evaluacion es {}",
//                    docenteSeccion.getSeccion().getTipoSeccionEnum().name(),
//                    evaluacion.getTipoSeccionEnum().name());
//
//            if (docenteSeccion.getSeccion().getTipoSeccionEnum().getTipoSeccionEvalEnum().equals(
//                    evaluacion.getTipoSeccionEnum())) {
//                logger.debug("El tipo de evaluacion {}", evaluacion.getTipoEvaluacion().getNombre());
//                evaluacionesByTipoSeccion.add(evaluacion);
//            }
//        }
        List<Evaluacion> evaluacionesBySeccionFinal = new ArrayList<>();
        for (Evaluacion eva : evaluacionesBySeccion) {
            if (!eva.isDesagregado() && eva.getEvaluacionSuperior() == null) {
                logger.debug("no esta desagregado");
                evaluacionesBySeccionFinal.add(eva);
            }
            if (eva.isDesagregado()) {
                logger.debug("esta desagregado");
                if (eva.getEvaluaciones() == null || eva.getEvaluaciones().isEmpty()) {
                    continue;
                }
                logger.debug("hijos {}", eva.getEvaluaciones().size());
                for (Evaluacion evaChild : eva.getEvaluaciones()) {

                    StringBuilder codigo = new StringBuilder();
                    codigo.append("(");
                    codigo.append(eva.getTipoEvaluacion().getCodigo());
                    codigo.append(")");
                    codigo.append(evaChild.getTipoEvaluacion().getCodigo());
                    logger.debug("nombre {}", codigo);

                    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(evaChild.getTipoEvaluacion().getId());
                    tipoEvaluacion.setNombre(evaChild.getTipoEvaluacion().getNombre());
                    tipoEvaluacion.setCodigo(codigo.toString());
                    evaChild.setTipoEvaluacion(tipoEvaluacion);

                    evaluacionesBySeccionFinal.add(evaChild);

                }
            }
        }
        logger.debug("cantidad de evaluaciones final {}", evaluacionesBySeccionFinal.size());

        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(docenteSeccion.getSeccion());
        logger.debug("matriculas seccion size {}", matriculasSeccionByFilter.size());

        Map<String, String> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(docenteSeccion.getSeccion().getId());

        //model.addAttribute("evaluacionesByTipoSeccion", evaluacionesByTipoSeccion);
        model.addAttribute("evaluacionesByTipoSeccion", evaluacionesBySeccionFinal);
        model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
        model.addAttribute("notas", mapNotas);

        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("{evaluacion}/evaluacion")
    public String evaluacion(@PathVariable("evaluacion") Long idEvaluacion, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Evaluacion eval = new Evaluacion();
        eval.setTipoEvaluacion(new TipoEvaluacion());
        eval.getTipoEvaluacion().setCodigo("PC1");
        model.addAttribute("evaluacion", eval);

        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("detalleCambioNota")
    public String detalleCambioNota(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "app/academico/docente/cargaacademica/detalleCambioNota";
    }

    @RequestMapping("unalm")
    public String unalm() {

        return "app/unalm/unalm";
    }

    @RequestMapping("detalleNotasAcademicas")
    public String detalleNotasAcademicas(Model model,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("evaluacion {}", evaluacionId);
        Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);

        return "app/academico/docente/cargaacademica/detalleNotasAcademicas";
    }

    @ResponseBody
    @RequestMapping("aceptarExpansion")
    public JsonResponse aceptarExpansion(@ModelAttribute("evaluacionSeccionId") Long evaluacionSeccionId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "Expandido correctamente.";
            cargaAcademicaService.aceptarExpansion(evaluacionSeccionId, ds);
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
    @RequestMapping("aceptarRechazo")
    public JsonResponse aceptarRechazo(
            @RequestParam("cursoId") Long cursoId,
            @RequestParam("seccionId") Long seccionId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Curso {}, Seccion {}", cursoId, seccionId);
            String message = "Rechazado correctamente.";

            cargaAcademicaService.aceptarRechazo(cursoId, seccionId, ds);

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
    @RequestMapping("aceptarPropuesta")
    public JsonResponse aceptarPropuesta(
            @RequestParam("cursoId") Long cursoId,
            @RequestParam("seccionId") Long seccionId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Curso {}, Seccion {}", cursoId, seccionId);
            String message = "Aceptado correctamente.";

            cargaAcademicaService.aceptarPlanCalificacion(cursoId, seccionId, ds);

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
    @RequestMapping("getEvaluacion")
    public JsonResponse getEvaluacion(
            Model model,
            @RequestParam(name = "docenteSeccion", required = true) Long idDocenteSeccion,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = cargaAcademicaService.getDetalleEvaluacion(evaluacionId, idDocenteSeccion);

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("activarEvaluacion")
    public JsonResponse activarEvaluacion(
            Model model,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            @RequestParam(name = "fechaEvaluacion", required = true) Date fechaEvaluacion,
            @RequestParam(name = "activacion", required = true) boolean activacion,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        logger.debug("activacion {}", activacion);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("evaluacion {}, Fecha evauacion {}", evaluacionId, fechaEvaluacion);
            Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);
            logger.debug("evaluacion param {}, {}", evaluacionId, evaluacion == null ? "no encontro" : "si encontro");

            evaluacion.setFechaRealizada(fechaEvaluacion);
            cargaAcademicaService.updateEvaluacion(evaluacion);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("evaSeleccionada", evaluacion.getTipoEvaluacion().getCodigo() + evaluacion.getNumero());
            node.put("evaId", evaluacion.getId());
            response.setData(node);
            if (activacion) {
                response.setMessage("Evaluación activada.");
            } else {
                response.setMessage("Fecha evaluación modificada.");
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    @ResponseBody
    @RequestMapping("saveIngresoNotas")
    public JsonResponse saveIngresoNotas(
            @RequestBody AlumnoEvaluacion[] alumnoEvaluaciones,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Notas {}", alumnoEvaluaciones.length);

            Evaluacion evaluacion = new Evaluacion(alumnoEvaluaciones[0].getEvaluacion().getId());
            evaluacion = cargaAcademicaService.findEvaluacion(evaluacion.getId());
            cargaAcademicaService.saveIngresoNotas(ds, evaluacion, alumnoEvaluaciones);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("evaSeleccionada", evaluacion.getTipoEvaluacion().getCodigo() + evaluacion.getNumero());
            node.put("evaId", evaluacion.getId());
            response.setData(node);
            response.setMessage("Notas ingresadas.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

}
