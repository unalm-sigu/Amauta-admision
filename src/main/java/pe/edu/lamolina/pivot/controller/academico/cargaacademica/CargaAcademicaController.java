package pe.edu.lamolina.pivot.controller.academico.cargaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.OrigenPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEvalEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfService;

@Controller
@RequestMapping("academico/docente/cargaacademica")
public class CargaAcademicaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Autowired
    PdfService pdfService;

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
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGrupoByDocente(ds.getDocente(), ciclo);
            logger.debug("Lista grupos por docente {}", gruposSeccion.size());

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoSeccion.getId());
                node.put("idCurso", grupoSeccion.getCurso().getId());

                node.put("nombre", grupoSeccion.getCurso().getNombre());
                node.put("codigo", grupoSeccion.getCurso().getCodigo());
                node.put("tpc", grupoSeccion.getCurso().getTpc());
                //(String) ObjectUtil.getParentTree(docSeccion, "seccion.aula.nombre")
                String secciones = "";
                for (Seccion seccion : grupoSeccion.getSecciones()) {
                    secciones += seccion.getId() + "|" + seccion.getCodigo() + ",";
                }
                node.put("secciones", secciones.substring(0, secciones.length() - 1));
                node.put("tienePlanCalificacion", false);
                if (grupoSeccion.getPlanCalificacion() == null) {
                    node.put("idSistemaCalificacion", "");
                    node.put("sistemaCalificacion", "");

                    node.put("estado", "");
                    node.put("estadoEnum", "");
                    if (ObjectUtil.getParentTree(grupoSeccion, "curso.planCalificacion.id") != null) {
                        node.put("idSistemaCalificacion", grupoSeccion.getCurso().getPlanCalificacion().getId().toString());
                        node.put("sistemaCalificacion", grupoSeccion.getCurso().getPlanCalificacion().getCodigo());

                        node.put("estado", EstadoPlanCalificaEnum.PRO.name());
                        node.put("estadoEnum", EstadoPlanCalificaEnum.PRO.getValue());
                    }
                } else {
                    node.put("idSistemaCalificacion", grupoSeccion.getPlanCalificacion().getId().toString());
                    node.put("sistemaCalificacion", grupoSeccion.getPlanCalificacion().getCodigo());

                    node.put("estado", grupoSeccion.getEstadoPlan());
                    node.put("estadoEnum", grupoSeccion.getEstadoPlanEnum().getValue());
                    node.put("tienePlanCalificacion", true);
                }

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
            }
            /*
            List<DocenteSeccion> lista = cargaAcademicaService.allByCargaAcademica(filter, ds.getDocente(), ciclo);
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
                node.put("aula", (String) ObjectUtil.getParentTree(docSeccion, "seccion.aula.nombre"));
                node.put("tipoSeccion", docSeccion.getSeccion().getTipoSeccion());
                node.put("alumnos", docSeccion.getSeccion().getMatriculados());
                node.put("horasSemanales", docSeccion.getSeccion().getHorasSemanales());
                node.put("estado", docSeccion.getEstado());
                node.put("estadoEnum", docSeccion.getEstadoEnum().getValue());
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
            }
             */
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
                node.put("notasIngresadas", evaluacionPlan.isNotasIngresadas());
                node.put("tipoSeccion", evaluacionPlan.getTipoSeccionEnum().getValue());
                array.add(node);

                for (EvaluacionExpandida evaluacionHija : evaluacionPlan.getEvaluacionesExpandidas()) {
                    ObjectNode nodeHijo = new ObjectNode(JsonNodeFactory.instance);

                    logger.debug("Tipo evaluacion {}", evaluacionHija.getTipoEvaluacion().getNombre() + " " + evaluacionHija.getNumero());
                    nodeHijo.put("evaPlanId", evaluacionHija.getId());
                    nodeHijo.put("tipoEvalCod", evaluacionHija.getTipoEvaluacion().getCodigo());
                    nodeHijo.put("tipoEvalNombre", evaluacionHija.getTipoEvaluacion().getNombre() + " " + evaluacionHija.getNumero());
                    nodeHijo.put("numero", evaluacionHija.getNumero());
                    nodeHijo.put("pesoEvaluacion", evaluacionHija.getPeso());
                    nodeHijo.put("esHijo", true);
                    nodeHijo.put("desagregado", evaluacionHija.isDesagregado());
                    nodeHijo.put("notasIngresadas", evaluacionPlan.isNotasIngresadas());
                    nodeHijo.put("tipoSeccion", evaluacionPlan.getTipoSeccionEnum().getValue());
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

    @RequestMapping("{sistemaCalificacion}/{grupoSeccion}/detalleSistemaCalificacion")
    public String detalleSistemaCalificacion(@PathVariable("sistemaCalificacion") Long idSistemaCalificacion,
            @PathVariable("grupoSeccion") Long idGrupoSeccion,
            Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("plan calificacion {}, grupo seccion {}", idSistemaCalificacion, idGrupoSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupoSeccion);
        PlanCalificacion planCalificacion = cargaAcademicaService.findPlanCalificacion(idSistemaCalificacion);
        // model.addAttribute("seccion", seccion);
        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("grupoSeccion", grupoSeccion);
        return "app/academico/docente/cargaacademica/detalleSistemaCalificacion";
    }

    @RequestMapping("expandir/{grupoSeccion}")
    public String expandir(Model model, HttpSession session, @PathVariable("grupoSeccion") Long grupoSeccionId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(grupoSeccionId);

        logger.debug("El grupo seccion es {}", grupoSeccion.getId());

        StringBuilder claves = new StringBuilder();
        boolean permiteAisgnar = false;
        DocenteSeccion docenteSeccion = null;

        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");
            if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                docenteSeccion = cargaAcademicaService.findDocenteSeccionByFilter(ds.getDocente(), sec);
                if (docenteSeccion != null) {
                    if (docenteSeccion.esDocentePrincipal()) {
                        permiteAisgnar = true;
                    }
                }
            }

        }

        model.addAttribute("permiteAsignar", permiteAisgnar);
        model.addAttribute("planCalificacion", grupoSeccion.getPlanCalificacion());
        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));
        model.addAttribute("grupoSeccion", grupoSeccion);

        //   Long idPlanCalificacion = seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId();
        Long idGrupoSeccion = grupoSeccion.getId();
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, idGrupoSeccion);
        model.addAttribute("evaluacionSeccion", evalSeccion);
        logger.debug("la evaluacion seccion es {}", evalSeccion.getId());
        cargaAcademicaService.createEvaluacionExpPorEvalSeccion(evalSeccion, EstadoPlanCalificaEnum.ACEP);

        return "app/academico/docente/cargaacademica/expandirSistemaCalificacion";
    }

    @RequestMapping("nuevo/{grupo}")
    public String nuevo(Model model, HttpSession session, @PathVariable("grupo") Long idGrupo) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupo);
        // PlanCalificacion planCalificacion = cargaAcademicaService.findPlanCalificacion(idPlanCalificacion);

        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo());
            claves.append(",");

        }

        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));

        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId());
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
    @RequestMapping("saveAsignarDocente")
    public JsonResponse saveAsignarDocente(Model model,
            @ModelAttribute EvaluacionExpandida evaluacionExpandida,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            cargaAcademicaService.saveAsignacionDocentes(evaluacionExpandida, ds);

            /*
            List<Evaluacion> evaluaciones = cargaAcademicaService.allEvaluacionesByEvalSeccion(evaluacion.getEvaluacionSeccion());
            model.addAttribute("dntEvaluacionPlan", evaluaciones);
            session.setAttribute("dntEvaluacionPlan", evaluaciones);
             */
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setSuccess(true);
            response.setMessage("Docentes Asignados");
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

                    ObjectNode jobj = new ObjectNode(JsonNodeFactory.instance);
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
        logger.debug("la evaluacion expandida es {}", evaluacionId);
        EvaluacionExpandida evaluacion = cargaAcademicaService.findEvaluacionExpandida(evaluacionId);
        List<TipoEvaluacion> lstTipoEvas = cargaAcademicaService.allTipoEvaluacion();
        /*
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
        }*/

        model.addAttribute("tipoEvaluaciones", lstTipoEvas);
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("evaluaciones", evaluacion.getEvaluacionesExpandidas());
        model.addAttribute("tieneEvaluaciones", evaluacion.getEvaluacionesExpandidas() != null && !evaluacion.getEvaluacionesExpandidas().isEmpty() ? true : false);
        return "app/academico/docente/cargaacademica/detalleExpandirEvaluacion";
    }

    @RequestMapping("detalleAsignarDocente")
    public String detalleAsignarDocente(Model model, HttpSession session,
            @RequestParam(value = "evaluacion", required = false) Long evaluacionId,
            @RequestParam(value = "grupoSeccionId", required = false) Long grupoSeccionId) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("detalleAsignarDocente, evaluacion expandida es {}", evaluacionId);

        EvaluacionExpandida evaluacionExpandida = cargaAcademicaService.findEvaluacionExpandida(evaluacionId);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(grupoSeccionId);
        logger.debug("Grupo Seccion {}", grupoSeccion.getId());

        List<Evaluacion> evaluacionByEvalExp = cargaAcademicaService.allEvaluacionesByEvalExpandida(evaluacionExpandida);
        logger.debug("Cantidad de evaluaciones {}", evaluacionByEvalExp.size());

        List<DocenteSeccion> allDocenteSeccionByGrupo = cargaAcademicaService.allDocenteSeccionByGrupo(grupoSeccion);
        logger.debug("cantidad de docentes seccion por grupo {}", allDocenteSeccionByGrupo.size());

        DocenteSeccion docenteSeccionTCUR = null;

        for (DocenteSeccion docenteSeccion1 : allDocenteSeccionByGrupo) {
            if (docenteSeccion1.getSeccion().getTipoSeccionEnum().equals(TipoSeccionEnum.TCUR)) {
                docenteSeccionTCUR = docenteSeccion1;
            }
        }

        for (Evaluacion evaluacion1 : evaluacionByEvalExp) {
            evaluacion1.setDocentesSeccion(new ArrayList<>());

            evaluacion1.setNotasIngresadas(false);
            if (evaluacion1.getFechaIngresoNota() != null) {
                evaluacion1.setNotasIngresadas(true);
            }

            if (evaluacion1.getDocenteEvaluador() == null) {
                evaluacion1.setDocenteEvaluador(new Docente());
            }
            if (evaluacion1.getTipoSeccionEnum().equals(TipoSeccionEvalEnum.PRAC)) {
                if (docenteSeccionTCUR != null) {
                    evaluacion1.getDocentesSeccion().add(docenteSeccionTCUR);
                }
            }
            for (DocenteSeccion docenteSeccion : allDocenteSeccionByGrupo) {
                logger.debug("Docente Seccion, Seccion {}, Evaluacion SEccion Responsable {}",
                        docenteSeccion.getSeccion().getId(), evaluacion1.getSeccionResponsable().getId());
                if (docenteSeccion.getSeccion().getId().equals(evaluacion1.getSeccionResponsable().getId())) {
                    evaluacion1.getDocentesSeccion().add(docenteSeccion);
                }
                /*else {

                    DocenteSeccion docenteSeccion1 = new DocenteSeccion();
                    Docente docente = new Docente();
                    docente.setPersona(new Persona());
                    docenteSeccion1.setDocente(docente);
                    evaluacion1.getDocentesSeccion().add(docenteSeccion1);
                }*/
            }
        }

        evaluacionExpandida.setEvaluaciones(evaluacionByEvalExp);
        model.addAttribute("evaluacionExpandida", evaluacionExpandida);
        return "app/academico/docente/cargaacademica/detalleAsignarDocente";
    }

    @ResponseBody
    @RequestMapping("deleteExpansionHija")
    public JsonResponse deleteExpansionHija(@RequestParam("evaluacion") Long evaluacion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "Evaluación eliminada exitosamente.";
            cargaAcademicaService.deleteEvaluacionExpandida(evaluacion);
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

    @RequestMapping("{seccion}/notasAcademicas")
    public String notasAcademicas(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId());
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        logger.debug("Consultara notas por seccion");
        Map<String, String> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());

        Curso curso = grupoSeccion.getCurso();

        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(ds.getCicloAcademico(), curso);

        //     model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("sistemaNotas", evaluacionSeccion.getSistemaNotas());
        model.addAttribute("evaluacionesByTipoSeccion", evaluacionesBySeccionFinal);
        model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
        model.addAttribute("notas", mapNotas);
        model.addAttribute("matriculaCursoMap", matriculaCursoMap);

        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("{seccion}/notasAcademicasReload")
    public String notasAcademicasReload(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {

        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId());
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        logger.debug("Consultara notas por seccion");
        Map<String, String> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());

        Curso curso = grupoSeccion.getCurso();

        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(ds.getCicloAcademico(), curso);

        //     model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("sistemaNotas", evaluacionSeccion.getSistemaNotas());
        model.addAttribute("evaluacionesByTipoSeccion", evaluacionesBySeccionFinal);
        model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
        model.addAttribute("notas", mapNotas);
        model.addAttribute("matriculaCursoMap", matriculaCursoMap);

        return "app/academico/docente/cargaacademica/notasAcademicasReload";
    }

    @RequestMapping("reporteDeActas")
    public void reporteDeActas(HttpServletResponse response,
            @RequestParam("seccion") Long idSeccion,
            Model model,
            HttpSession session) throws IOException {

        logger.debug("docente seccion {}", idSeccion);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion secc = cargaAcademicaService.findSeccion(idSeccion);
        Curso cur = secc.getGrupoSeccion().getCurso();
        String nom = "ActaNotas_" + cur.getCodigo() + "_" + secc.getCodigo();

        List<String> lstPdfFiles = pdfService.reporteDeActaDeNotas(secc.getGrupoSeccion().getId(), ds);

        String fileNameRoot = pdfService.concatPDFs(lstPdfFiles, nom, false);
        if (!fileNameRoot.isEmpty()) {
            File filex = new File(fileNameRoot);
            if (!filex.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.reset();
            response.setBufferSize(Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + nom + ".pdf\"");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(filex), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                output = new BufferedOutputStream(response.getOutputStream(), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                IOUtils.copy(input, output);
                response.flushBuffer();

            } finally {

                close(output);
                close(input);

            }
        }
    }

    @RequestMapping("{evaluacion}/evaluacion")
    public String evaluacion(@PathVariable("evaluacion") Long idEvaluacion, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        /*
        Evaluacion eval = new Evaluacion();
        eval.setTipoEvaluacion(new TipoEvaluacion());
        eval.getTipoEvaluacion().setCodigo("PC1");
         */

        Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(idEvaluacion);
        model.addAttribute("evaluacion", evaluacion);
        return "app/academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("detalleCambioNota")
    public String detalleCambioNota(Model model, HttpSession session,
            @RequestParam(name = "matriculaSeccion") Long matriculaSeccionId,
            @RequestParam(name = "nsp") boolean nsp) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        logger.debug("matricula seccion {}", matriculaSeccionId);
        MatriculaSeccion matriculaSeccion = cargaAcademicaService.findMatriculaSeccion(matriculaSeccionId);
        logger.debug("alumno {}", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona().getNombreCompleto());
        logger.debug("curso {}", matriculaSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());

        model.addAttribute("alumno", matriculaSeccion.getMatriculaResumen().getAlumno());
        model.addAttribute("alumnoPer", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona());
        model.addAttribute("curso", matriculaSeccion.getSeccion().getGrupoSeccion().getCurso());
        model.addAttribute("seccion", matriculaSeccion.getSeccion());

        List<AlumnoEvaluacion> alumnosEvaluaciones = cargaAcademicaService.allEvaluacionsByFilter(matriculaSeccion.getMatriculaResumen().getAlumno(),
                matriculaSeccion.getSeccion().getGrupoSeccion().getCurso(), cicloAcademico);
        List<Evaluacion> evaluacionesDisponibles = new ArrayList<>();

        for (AlumnoEvaluacion alumnoEvaluacion : alumnosEvaluaciones) {
            if (nsp) {
                if (alumnoEvaluacion.getNota().equals(AlumnoEvaluacion.NSP)) {
                    evaluacionesDisponibles.add(alumnoEvaluacion.getEvaluacion());
                }
            } else {
                logger.debug("");
                if (!alumnoEvaluacion.getNota().equals(AlumnoEvaluacion.NSP)) {
                    evaluacionesDisponibles.add(alumnoEvaluacion.getEvaluacion());
                }
            }
        }
        model.addAttribute("evaluacionesDisp", evaluacionesDisponibles);

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
            @RequestParam("grupoId") Long grupoId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Curso {}, Grupo {}", cursoId, grupoId);
            String message = "Rechazado correctamente.";

            cargaAcademicaService.aceptarRechazo(cursoId, grupoId, ds);

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
            @RequestParam("grupoId") Long grupoId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Curso {}, Grupo {}", cursoId, grupoId);
            String message = "Aceptado correctamente.";

            cargaAcademicaService.aceptarPlanCalificacion(cursoId, grupoId, ds);

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
            @RequestParam(name = "seccion", required = true) Long seccionId,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ObjectNode node = cargaAcademicaService.getDetalleEvaluacion(evaluacionId, seccionId);

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

            Evaluacion evaluacion = cargaAcademicaService.activarEvaluacion(evaluacionId, fechaEvaluacion, ds);

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

    @ResponseBody
    @RequestMapping("solicitarCambio")
    public JsonResponse solicitarCambio(
            ReclamoNota reclamoNota,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Alumno {}", reclamoNota.getAlumno().getId());
            logger.debug("Evaluacion {}", reclamoNota.getEvaluacion().getId());
            logger.debug("Motivo {}", reclamoNota.getMotivo());
            logger.debug("nota inicial {}, nota final {}", reclamoNota.getNotaInicial(), reclamoNota.getNotaFinal());

            cargaAcademicaService.saveReclamoNota(reclamoNota, ds);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            response.setData(node);
            response.setMessage("Modificación ingresada.");
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
    @RequestMapping("cambiarEvaluacion")
    public JsonResponse cambiarEvaluacion(
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            @RequestParam(name = "alumno", required = true) Long alumnoId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("la evaluacion es {}", evaluacionId);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            if (evaluacionId != null && alumnoId != null) {
                AlumnoEvaluacion alumnoEvaluacion = cargaAcademicaService.findAlumnoEvaluacion(null, evaluacionId, alumnoId);
                node.put("nota", alumnoEvaluacion.getNota());
                node.put("notaNumerica", alumnoEvaluacion.getValorNumerico());
            } else {
                node.put("nota", "");
                node.put("notaNumerica", "");
            }

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

}
