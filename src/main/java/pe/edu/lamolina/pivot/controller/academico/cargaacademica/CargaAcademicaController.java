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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.controller.reporte.view.ReporteActasView;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.NotaLetra;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.model.academico.TipoEvaluacion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
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

    @Autowired
    ReporteActasView reporteActasView;

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
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        logger.debug("el docente logeado es {}", ds.getDocente().getId());
        //    cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);

        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "academico/docente/cargaacademica/cargaAcademica";
    }

    @RequestMapping("sistemaCurso")
    public String sistemaCurso(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "academico/docente/cargaacademica/sistemaCurso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = ds.getDocente();

            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGrupoByDocente(docente, ciclo, ds);
            logger.debug(this.getClass() + " Lista grupos por docente {}", gruposSeccion.size());

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoSeccion.getId());
                node.put("idCurso", grupoSeccion.getCurso().getId());
                node.put("tipoCiclo", grupoSeccion.getCicloAcademico().getTipoCicloEnum().getValue());
                node.put("nombre", grupoSeccion.getCurso().getNombre());
                node.put("codigo", grupoSeccion.getCurso().getCodigo());
                node.put("tpc", grupoSeccion.getCurso().getTpc());
                node.put("responsable", (String) ObjectUtil.getParentTree(grupoSeccion.getDocenteResponsable(), "persona.nombreCompleto"));
                node.put("codigo", grupoSeccion.getCurso().getCodigo());
                node.put("estadoGrupoEnum", grupoSeccion.getEstadoGrupoEnum().getValue());
                //(String) ObjectUtil.getParentTree(docSeccion, "seccion.aula.nombre")
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                String secciones = "";

                for (Seccion seccion : grupoSeccion.getSecciones()) {
                    secciones += seccion.getId() + "|" + seccion.getCodigo2() + "|";

                    if (ObjectUtil.getParentTree(seccion, "grupoHoras") != null) {
                        secciones += seccion.getGrupoHoras().getId() + "|" + seccion.getGrupoHoras().getCodigo() + "|";
                        //grupoHoras += seccion.getGrupoHoras().getId() + "|" + seccion.getGrupoHoras().getCodigo() + ",";
                    } else {
                        secciones += " | |";
                    }
                    secciones += (seccion.getVerInformacion() ? "VER" : "NO-VER") + ",";
                }
                node.put("secciones", secciones.substring(0, secciones.length() - 1));
//                if (!"".equals(grupoHoras)) {
//                    grupoHoras = grupoHoras.substring(0, grupoHoras.length() - 1);
//                }
//                node.put("grupoHoras", grupoHoras);

                boolean tienePlanCalificacion = false;
                boolean verOpciones = false;
                boolean propuesto = false;
                PlanCalificacion planCalificacionSelected = null;
                node.put("sistemas", "");

                List<PlanCalificacionCurso> planesCalificacionesCursos = grupoSeccion.getCurso().getPlanesCalificacionCursos();

                StringBuilder strbSistemas = new StringBuilder();
                logger.debug("Curso {}, Cantidad Plan Cursos {}", grupoSeccion.getCurso().getId(), planesCalificacionesCursos.size());

                if (grupoSeccion.getPlanCalificacion() == null || grupoSeccion.isEstadoPropuesto()) {
                    logger.debug("El grupo no tiene plan calificacion o su estado es propuesto");
                    if (planesCalificacionesCursos.isEmpty()) {
                        logger.debug("sin planes asociados al curso");
                        node.put("estado", EstadoPlanCalificaEnum.PEND.name());
                        node.put("estadoEnum", EstadoPlanCalificaEnum.PEND.getValue());
                    } else {
                        logger.debug("con planes asociados al curso, quedara como propuesto");
                        for (PlanCalificacionCurso planesCalificacionesCurso : planesCalificacionesCursos) {
                            strbSistemas.append(planesCalificacionesCurso.getPlanCalificacion().getId());
                            strbSistemas.append(",");
                            strbSistemas.append(planesCalificacionesCurso.getPlanCalificacion().getCodigo());
                            strbSistemas.append("-");
                        }
                        if (strbSistemas.length() != 0) {
                            node.put("sistemas", strbSistemas.substring(0, strbSistemas.length() - 1));
                        }

                        node.put("estado", EstadoPlanCalificaEnum.PRO.name());
                        node.put("estadoEnum", EstadoPlanCalificaEnum.PRO.getValue());
                        propuesto = true;
                        verOpciones = true;
                    }

                } else {
                    verOpciones = true;
                    node.put("idSistemaCalificacion", grupoSeccion.getPlanCalificacion().getId().toString());
                    node.put("sistemaCalificacion", grupoSeccion.getPlanCalificacion().getCodigo());

                    node.put("estado", grupoSeccion.getEstadoPlan());
                    node.put("estadoEnum", grupoSeccion.getEstadoPlanEnum().getValue());

                    tienePlanCalificacion = true;
                    planCalificacionSelected = grupoSeccion.getPlanCalificacion();
                }
                node.put("tienePlanCalificacion", tienePlanCalificacion);

                node.put("verDetalleSistemaCal", false);
                node.put("verOpciones", verOpciones);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoSolicitado()
                            || grupoSeccion.isEstadoExpandido()
                            || grupoSeccion.isEstadoExpandir()) {
                        node.put("verDetalleSistemaCal", true);
                    }
                }
                node.put("verAceptarSistemaCal", false);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoPropuesto() || propuesto) {
                        node.put("verAceptarSistemaCal", true);
                    }
                }
                array.add(node);
            }

            json.setData(array);
            json.setTotal(gruposSeccion.size());
            json.setFiltered(gruposSeccion.size());

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
            EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvaluacionSeccion(evaluacionSeccionId);
            GrupoSeccion grupoSeccion = evaluacionSeccion.getGrupoSeccion();

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<EvaluacionExpandida> lstEvaluacionPlan = cargaAcademicaService.allEvaluacionesExpByEvalSeccion(new EvaluacionSeccion(evaluacionSeccionId));
            //  List<Evaluacion> lstEvaluacionPlan = dntEvaluacionPlan;
            logger.debug("Lista {}", lstEvaluacionPlan.size());
            boolean editarPorcentajeGeneral = false;

            for (EvaluacionExpandida evaluacionExpandida : lstEvaluacionPlan) {
                logger.debug("Padre - Tipo evaluacion {}", evaluacionExpandida.getTipoEvaluacion().getNombre() + " " + evaluacionExpandida.getNumero());

                ObjectNode node = castEvaluacionExpandida(evaluacionExpandida);
                node.put("esAbuelo", true);
                node.put("esHijo", false);
                node.put("esNieto", false);
                boolean estaEvaluado = false;

                node.put("editarPorcentaje", evaluacionExpandida.isPorcentajeVariable() && !estaEvaluado && !evaluacionExpandida.isDesagregado());
                if (evaluacionExpandida.isPorcentajeVariable() && !estaEvaluado && !evaluacionExpandida.isDesagregado()) {
                    editarPorcentajeGeneral = true;
                }
                for (Evaluacion eval : evaluacionExpandida.getEvaluaciones()) {
                    estaEvaluado = (eval.getFechaIngresoNota() != null);
                }
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                array.add(node);
                /*
                BigDecimal totalHija = BigDecimal.ZERO;
                for (EvaluacionExpandida evaluacionHija : evaluacionExpandida.getEvaluacionesExpandidas()) {
                    totalHija = totalHija.add(evaluacionHija.getPeso());
                }*/

                for (EvaluacionExpandida evaluacionHija : evaluacionExpandida.getEvaluacionesExpandidas()) {
                    logger.debug("Hija - Tipo evaluacion {}", evaluacionHija.getTipoEvaluacion().getNombre() + " " + evaluacionHija.getNumero());
                    ObjectNode nodeHijo = castEvaluacionExpandida(evaluacionHija);
                    //            nodeHijo.put("editarPorcentaje", evaluacionHija.isPorcentajeVariable() && !estaEvaluado && !evaluacionHija.isDesagregado());
                    nodeHijo.put("editarPorcentaje", false);
                    nodeHijo.put("esHijo", true);
                    nodeHijo.put("esNieto", false);
                    nodeHijo.put("esAbuelo", false);
                    /*
                    if (totalHija.compareTo(evaluacionExpandida.getPeso()) == 0) {
                        nodeHijo.put("porcentajeFail", false);
                    } else {
                        nodeHijo.put("porcentajeFail", true);
                    }*/
                    nodeHijo.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                    array.add(nodeHijo);
                    /*
                    BigDecimal totalNietas = BigDecimal.ZERO;
                    for (EvaluacionExpandida evaluacionNieta : evaluacionHija.getEvaluacionesExpandidas()) {
                        totalNietas = totalNietas.add(evaluacionNieta.getPeso());
                    }*/

                    for (EvaluacionExpandida evaluacionNieta : evaluacionHija.getEvaluacionesExpandidas()) {
                        logger.debug("Nieta - Tipo evaluacion {}", evaluacionNieta.getTipoEvaluacion().getNombre() + " " + evaluacionNieta.getNumero());
                        ObjectNode nodeNieta = castEvaluacionExpandida(evaluacionNieta);
                        //            nodeNieta.put("editarPorcentaje", evaluacionExpandida.isPorcentajeVariable() && !estaEvaluado && !evaluacionExpandida.isDesagregado());
                        nodeNieta.put("editarPorcentaje", false);
                        nodeNieta.put("esNieto", true);
                        nodeNieta.put("esAbuelo", false);
                        /*
                        if (totalNietas.compareTo(evaluacionHija.getPeso()) == 0) {
                            nodeNieta.put("porcentajeFail", false);
                        } else {
                            nodeNieta.put("porcentajeFail", true);
                        }*/

                        nodeNieta.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                        array.add(nodeNieta);
                    }

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

    public ObjectNode castEvaluacionExpandida(EvaluacionExpandida evaluacionExpandida) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("evaPlanId", evaluacionExpandida.getId());
        node.put("tipoEvalCod", evaluacionExpandida.getTipoEvaluacion().getCodigo());
        node.put("tipoEvalNombre", evaluacionExpandida.getTipoEvaluacion().getNombre() + " Nº " + evaluacionExpandida.getNumero());
        node.put("numero", evaluacionExpandida.getNumero());
        node.put("pesoEvaluacion", NumberFormat.precio(evaluacionExpandida.getPeso()));
        boolean esPadre = !evaluacionExpandida.getEvaluacionesExpandidas().isEmpty();
        node.put("esPadre", esPadre);
        node.put("desagregado", evaluacionExpandida.isDesagregado());
        node.put("notasIngresadas", evaluacionExpandida.isNotasIngresadas());
        node.put("tipoSeccion", evaluacionExpandida.getTipoSeccionEvalEnum().getValue());
        node.put("conNotas", evaluacionExpandida.isNotasIngresadas());
        node.put("permiteAnular", esPadre && evaluacionExpandida.isDesagregado());
        node.put("notaMinAnulable", evaluacionExpandida.getNotaMinimaAnulable());
        node.put("estadoPlanExp", evaluacionExpandida.getEstadoEnum().getValue());
        node.put("estadoAnulado", evaluacionExpandida.isEstadoAnulado());
        {
            ArrayNode evaluadores = new ArrayNode(JsonNodeFactory.instance);
            List<Evaluacion> evals = evaluacionExpandida.getEvaluaciones();
            for (Evaluacion eval : evals) {
                Seccion seccion = eval.getSeccionResponsable();
                Docente profe = eval.getDocenteEvaluador();

                ObjectNode nodeDoc = new ObjectNode(JsonNodeFactory.instance);
                nodeDoc.put("seccion", seccion.getCodigo2());
                nodeDoc.put("docente", profe == null ? "" : (profe.getPersona().getApellidosNombres()));

                evaluadores.add(nodeDoc);
            }
            node.put("evaluadores", evaluadores);
        }

        {
            ArrayNode tipoSeccionesEval = new ArrayNode(JsonNodeFactory.instance);
            List<TipoSeccionEvalEnum> tipos = TipoSeccionEvalEnum.list;
            for (TipoSeccionEvalEnum sec : tipos) {
                ObjectNode nodeSec = new ObjectNode(JsonNodeFactory.instance);
                nodeSec.put("codigo", sec.name());
                nodeSec.put("nombre", sec.getValue());
                nodeSec.put("selected", (sec.name().equals(evaluacionExpandida.getTipoSeccionEvalEnum().name())));
                tipoSeccionesEval.add(nodeSec);
            }
            node.put("tipoSeccionesEval", tipoSeccionesEval);
        }

        return node;
    }

    @RequestMapping("{sistemaCalificacion}/{grupoSeccion}/detalleSistemaCalificacion")
    public String detalleSistemaCalificacion(
            @PathVariable("sistemaCalificacion") Long idSistemaCalificacion,
            @PathVariable("grupoSeccion") Long idGrupoSeccion, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("plan calificacion {}, grupo seccion {}", idSistemaCalificacion, idGrupoSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupoSeccion);
        PlanCalificacion planCalificacion = cargaAcademicaService.findPlanCalificacion(idSistemaCalificacion);
        List<Curso> cursosByPlan = cargaAcademicaService.allActiveCursosByPlan(planCalificacion);

        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("grupoSeccion", grupoSeccion);

        model.addAttribute("tieneCursos", (!cursosByPlan.isEmpty()));

        return "academico/docente/cargaacademica/detalleSistemaCalificacion";
    }
//      @PathVariable("sistemaCalificacion") Long idSistemaCalificacion, {sistemaCalificacion}/

    @RequestMapping("{grupoSeccion}/aceptarSistemaCalificacion")
    public String aceptarSistemaCalificacion(
            @PathVariable("grupoSeccion") Long idGrupoSeccion,
            @RequestParam(name = "planCalificacion", required = false) Long planCalificacion,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("grupo seccion {}", idGrupoSeccion);
        logger.debug("planCalificacion {}", planCalificacion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupoSeccion);

        List<PlanCalificacionCurso> planesCalificacionCurso = cargaAcademicaService.findAllActivePlanCalificacionCursos(grupoSeccion.getCurso(),
                ds.getCicloAcademico().getTipoCicloEnum());
        PlanCalificacion planCalifica = planesCalificacionCurso.get(0).getPlanCalificacion();
        if (planCalificacion != null) {
            logger.debug("buscara el sistema calificacion");
            planCalifica = cargaAcademicaService.findPlanCalificacion(planCalificacion);
        }

        model.addAttribute("planCalificacion", planCalifica);
        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("planesCalificacionCurso", planesCalificacionCurso);
        model.addAttribute("tieneCursos", false);

        return "academico/docente/cargaacademica/aceptarSistemaCalificacion";
    }

    @RequestMapping("expandir/{grupoSeccion}")
    public String expandir(Model model, HttpSession session, @PathVariable("grupoSeccion") Long grupoSeccionId) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(grupoSeccionId);
        logger.debug("El grupo seccion es {}", grupoSeccion.getId());

        logger.debug("El docente es {}", ds.getDocente().getId());

        List<EvaluacionPlan> evaluacionPlanes = cargaAcademicaService.allEvaluacionPlanByPlanCalifica(grupoSeccion.getPlanCalificacion().getId());
        ObjectNode evalPlanJson = new ObjectNode(JsonNodeFactory.instance);
        for (EvaluacionPlan evaPlanEach : evaluacionPlanes) {
            ObjectNode evaPlan2 = new ObjectNode(JsonNodeFactory.instance);
            evaPlan2.put("pesoTotal", evaPlanEach.getPesoTotal());
            evalPlanJson.put(evaPlanEach.getTipoEvaluacion().getCodigo(), evaPlan2.toString());
        }

        StringBuilder claves = new StringBuilder();
        boolean permiteAsignar = false;
        DocenteSeccion docenteSeccion = null;

        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo2());
            claves.append(",");
            if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                docenteSeccion = cargaAcademicaService.findDocenteSeccionByFilter(ds.getDocente(), sec);
                if (docenteSeccion != null && docenteSeccion.getEstadoEnum().equals(EstadoEnum.ACT)) {
                    if (docenteSeccion.esDocentePrincipal()) {
                        permiteAsignar = true;
                    }
                }
            }

        }

        model.addAttribute("permiteAsignar", permiteAsignar);
        model.addAttribute("planCalificacion", grupoSeccion.getPlanCalificacion());
        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("evalPlanJson", evalPlanJson);

        //   Long idPlanCalificacion = seccion.getGrupoSeccion().getCurso().getPlanCalificacion().getId();
        Long idGrupoSeccion = grupoSeccion.getId();
        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, idGrupoSeccion, null);
        model.addAttribute("evaluacionSeccion", evalSeccion);
        logger.debug("la evaluacion seccion es {}", evalSeccion.getId());
        cargaAcademicaService.createEvaluacionExpPorEvalSeccion(evalSeccion, EstadoPlanCalificaEnum.ACEP);

        return "academico/docente/cargaacademica/expandirSistemaCalificacion";
    }

    @RequestMapping("nuevo/{grupo}")
    public String nuevo(Model model, HttpSession session, @PathVariable("grupo") Long idGrupo) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupo);
        // PlanCalificacion planCalificacion = cargaAcademicaService.findPlanCalificacion(idPlanCalificacion);

        StringBuilder claves = new StringBuilder();
        for (Seccion sec : grupoSeccion.getSecciones()) {
            claves.append(sec.getCodigo2());
            claves.append(",");

        }

        model.addAttribute("curso", grupoSeccion.getCurso());
        model.addAttribute("claves", claves.substring(0, claves.length() - 1));

        EvaluacionSeccion evalSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        logger.debug("La evaluacion seccion es {}", evalSeccion != null ? evalSeccion.getId().toString() : "no se encontro");
        model.addAttribute("evaluacionSeccion", evalSeccion);

        PlanCalificacion planCalificacion = new PlanCalificacion();

        model.addAttribute("planCalificacion", planCalificacion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("tipoEvaluaciones", cargaAcademicaService.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", cargaAcademicaService.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEvalEnum.values());

        return "academico/docente/cargaacademica/nuevoSistemaCalificacion";
    }

    @ResponseBody
    @RequestMapping("saveExpandir")
    public JsonResponse saveExpandir(@ModelAttribute EvaluacionExpandida evaluacion, Model model, RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            cargaAcademicaService.saveExpansionEvaluacion(evaluacion, ds);

            /*
            List<MatriculaSeccion> alumnosSeccion = cargaAcademicaService.allMatriculaSeccionByFilter(evaluacion, ds.getCicloAcademico());
            int loop = 1;
            for (MatriculaSeccion ms : alumnosSeccion) {
                Seccion seccion = ms.getSeccion();
                GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
                Alumno alumno = ms.getMatriculaResumen().getAlumno();

                if (gpoSecc.getPlanCalificacion() == null) {
                    break;
                }

                if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    continue;
                }

                cargaAcademicaService.recalcularAllResumenEvalAlumno(alumno, gpoSecc, loop, ds);
                loop++;

            }
             */
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
                planCalificacion.setIdUserRegistro(ds.getUsuario().getId());
                cargaAcademicaService.saveSistemaCalifica(planCalificacion, grupoSeccionId, ds);
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
    public JsonResponse getSistemaNotas(
            @RequestParam("sistemaNotas") Long idSistemaNotas,
            @RequestParam("grupo") Long idGrupoSeccion,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Sistema Notas {}", idSistemaNotas);

            SistemaNotas sistemaNotas = cargaAcademicaService.findSistemaNotaById(idSistemaNotas);
            GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(idGrupoSeccion);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("esNumerico", sistemaNotas.isNumerico());
            node.put("esLetras", sistemaNotas.isLetras());
            node.put("valorInicial", sistemaNotas.getValorInicio());
            node.put("valorFinal", sistemaNotas.getValorFinal());
            node.put("minimoAprobatorio", sistemaNotas.getMinimoAprobatorio());
            node.put("letras", "");
            node.put("esCreditoZero", grupoSeccion.getCurso().isCreditosZero());
            node.put("esCreditoVariable", grupoSeccion.getCurso().isTieneCreditosVariables());

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

    @ResponseBody
    @RequestMapping("cambiarTipoSecEval")
    public JsonResponse cambiarTipoSecEval(Model model,
            @RequestParam(value = "tipoSeccionEval", required = true) String tipoSeccionEval,
            @RequestParam(value = "evaluacionExp", required = true) Long evaluacionExp,
            RedirectAttributes redirectAttr, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            cargaAcademicaService.cambiarTipoSeccionEvaluacion(new EvaluacionExpandida(evaluacionExp), TipoSeccionEvalEnum.valueOf(tipoSeccionEval));
            response.setMessage("Cambio de sección, correcto.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.ERROR_GENERAL);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarAnularNotaMinima")
    public JsonResponse cambiarAnularNotaMinima(Model model,
            @RequestParam(value = "notaMinimaAnulable", required = true) Integer notaMinimaAnulable,
            @RequestParam(value = "evaluacionExp", required = true) Long evaluacionExp,
            RedirectAttributes redirectAttr, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            logger.debug("Anular nota minima {}, Evaluacion Exp {}", notaMinimaAnulable, evaluacionExp);

            cargaAcademicaService.cambiarAnularNotaminima(new EvaluacionExpandida(evaluacionExp), notaMinimaAnulable);

            response.setMessage("Se cambio la anulación de nota mínima.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.ERROR_GENERAL);
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
        EvaluacionExpandida evaluacionExp = cargaAcademicaService.findEvaluacionExpandida(evaluacionId);
        List<TipoEvaluacion> lstTipoEvas = cargaAcademicaService.allTipoEvaluacion();

        model.addAttribute("tipoEvaluaciones", lstTipoEvas);
        model.addAttribute("evaluacion", evaluacionExp);
        model.addAttribute("evaluaciones", evaluacionExp.getEvaluacionesExpandidas());
        model.addAttribute("tieneEvaluaciones", evaluacionExp.getEvaluacionesExpandidas() != null && !evaluacionExp.getEvaluacionesExpandidas().isEmpty() ? true : false);
        return "academico/docente/cargaacademica/detalleExpandirEvaluacion";
    }

    @ResponseBody
    @RequestMapping("anularEvaluacionExp")
    public JsonResponse anularEvaluacionExp(@RequestParam("evaluacion") Long evaluacionExp,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            logger.debug("Evaluacion Expandida {}", evaluacionExp);

            cargaAcademicaService.anularEvaluacionExp(new EvaluacionExpandida(evaluacionExp));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Anulado correctamente.");

            //   List<MatriculaSeccion> matriculasSeccion = cargaAcademicaService.eliminarNotas(new Evaluacion(evaluacionId), ds);
            //   cargaAcademicaService.calcularNotasLista(matriculasSeccion, ds);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
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
                    if (!evaluacion1.getDocentesSeccion().contains(docenteSeccion)) {
                        evaluacion1.getDocentesSeccion().add(docenteSeccion);
                    }
                }
            }
        }

        evaluacionExpandida.setEvaluaciones(evaluacionByEvalExp);
        model.addAttribute("evaluacionExpandida", evaluacionExpandida);
        return "academico/docente/cargaacademica/detalleAsignarDocente";
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
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        Curso curso = grupoSeccion.getCurso();
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        logger.debug("El docente es {}", ds.getDocente().getId());

        logger.debug("Consultara notas por seccion");
        Map<String, AlumnoEvaluacion> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());

        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(ds.getCicloAcademico(), curso);

        boolean esDocentePrincipal = false;
        for (Seccion sec : grupoSeccion.getSecciones()) {

            if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                DocenteSeccion docenteSeccion = cargaAcademicaService.findDocenteSeccionByFilter(ds.getDocente(), sec);
                if (docenteSeccion != null && docenteSeccion.getEstadoEnum().equals(EstadoEnum.ACT)) {
                    if (docenteSeccion.esDocentePrincipal()) {
                        esDocentePrincipal = true;
                    }
                }
            }

        }

        //     model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("sistemaNotas", evaluacionSeccion.getSistemaNotas());

        model.addAttribute("evaluacionesByTipoSeccion", evaluacionesBySeccionFinal);
        model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
    //    model.addAttribute("matriculasCursoMap", matriculasCursoMap);
        model.addAttribute("notas", mapNotas);
        model.addAttribute("matriculaCursoMap", matriculaCursoMap);
        model.addAttribute("esDocentePrincipal", esDocentePrincipal);

        return "academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("{seccion}/alumnos")
    public String alumnos(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {
        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        logger.debug("Seccion {}, Grupo Seccion {}", seccion.getId(), seccion.getGrupoSeccion().getId());
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        logger.debug("El docente es {}", ds.getDocente().getId());
        logger.debug("Consultara notas por seccion");

        Map<String, AlumnoEvaluacion> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());
        Curso curso = grupoSeccion.getCurso();
        Map matriculaCursoMap = cargaAcademicaService.getMapMatriculasCursoByCicloCurso(ds.getCicloAcademico(), curso);

        boolean esDocentePrincipal = false;
        for (Seccion sec : grupoSeccion.getSecciones()) {

            if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                DocenteSeccion docenteSeccion = cargaAcademicaService.findDocenteSeccionByFilter(ds.getDocente(), sec);
                if (docenteSeccion != null && docenteSeccion.getEstadoEnum().equals(EstadoEnum.ACT)) {
                    if (docenteSeccion.esDocentePrincipal()) {
                        esDocentePrincipal = true;
                    }
                }
            }

        }

        //     model.addAttribute("docenteSeccion", docenteSeccion);
        model.addAttribute("seccion", seccion);
        model.addAttribute("grupoSeccion", grupoSeccion);
        model.addAttribute("curso", curso);
        model.addAttribute("sistemaNotas", evaluacionSeccion.getSistemaNotas());
        model.addAttribute("evaluacionesByTipoSeccion", evaluacionesBySeccionFinal);
        model.addAttribute("matriculasSeccion", matriculasSeccionByFilter);
        model.addAttribute("notas", mapNotas);
        model.addAttribute("matriculaCursoMap", matriculaCursoMap);
        model.addAttribute("esDocentePrincipal", esDocentePrincipal);
        return "academico/docente/alumnos/alumnos";
    }

    @RequestMapping("{seccion}/notasAcademicasReload")
    public String notasAcademicasReload(
            @PathVariable("seccion") Long idSeccion,
            Model model, HttpSession session) {

        logger.debug("la seccion es {}", idSeccion);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Seccion seccion = cargaAcademicaService.findSeccion(idSeccion);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(seccion.getGrupoSeccion().getId());
        EvaluacionSeccion evaluacionSeccion = cargaAcademicaService.findEvalSeccByPlanCalGrupoSec(null, grupoSeccion.getId(), null);
        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(seccion);
        List<MatriculaSeccion> matriculasSeccionByFilter = cargaAcademicaService.allMatriculaSeccionBySeccion(seccion);

        logger.debug("Consultara notas por seccion");
        Map<String, AlumnoEvaluacion> mapNotas = cargaAcademicaService.allAlumnoEvaluacionBySeccion(seccion.getId());

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

        return "academico/docente/cargaacademica/notasAcademicasReload";
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
        String nom = "ActaNotas_" + cur.getCodigo() + "_" + secc.getCodigo2();

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

    @RequestMapping("reporteDeActasExcel")
    public ModelAndView reporteDeActasExcel(Model model,
            @RequestParam("seccion") Long idSeccion,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Seccion secc = cargaAcademicaService.findSeccion(idSeccion);
        Curso cur = secc.getGrupoSeccion().getCurso();

        model.addAttribute("seccion", secc);
        model.addAttribute("grupoSeccion", secc.getGrupoSeccion());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("curso", cur);

        return new ModelAndView(reporteActasView);
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
        return "academico/docente/cargaacademica/notasAcademicas";
    }

    @RequestMapping("detalleCambioNota")
    public String detalleCambioNota(Model model, HttpSession session,
            @RequestParam(name = "matriculaSeccion") Long matriculaSeccionId,
            @RequestParam(name = "nsp") boolean nsp) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        logger.debug("matricula seccion {}", matriculaSeccionId);
        MatriculaSeccion matriculaSeccion = cargaAcademicaService.findMatriculaSeccion(matriculaSeccionId);
        GrupoSeccion grupoSeccion = cargaAcademicaService.findGrupo(matriculaSeccion.getSeccion().getGrupoSeccion().getId());
        logger.debug("alumno {}", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona().getNombreCompleto());
        logger.debug("curso {}", matriculaSeccion.getSeccion().getGrupoSeccion().getCurso().getNombre());

        model.addAttribute("alumno", matriculaSeccion.getMatriculaResumen().getAlumno());
        model.addAttribute("alumnoPer", matriculaSeccion.getMatriculaResumen().getAlumno().getPersona());
        model.addAttribute("curso", matriculaSeccion.getSeccion().getGrupoSeccion().getCurso());
        model.addAttribute("seccion", matriculaSeccion.getSeccion());
        model.addAttribute("sistemaNotas", grupoSeccion.getPlanCalificacion().getSistemaNotas());

        List<Evaluacion> evaluacionesBySeccionFinal = cargaAcademicaService.allEvaluacionesByTipoSeccion(matriculaSeccion.getSeccion());

        List<AlumnoEvaluacion> alumnosEvaluaciones = cargaAcademicaService.allEvaluacionsByFilter(matriculaSeccion.getMatriculaResumen().getAlumno(),
                matriculaSeccion.getSeccion().getGrupoSeccion().getCurso(), cicloAcademico);
        //evaluacionesDisponibles se muestra en el modal
        List<Evaluacion> evaluacionesDisponibles = new ArrayList<>();

        for (AlumnoEvaluacion alumnoEvaluacion : alumnosEvaluaciones) {

            if (!evaluacionesBySeccionFinal.contains(alumnoEvaluacion.getEvaluacion())) {
                continue;
            }
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

        return "academico/docente/cargaacademica/detalleCambioNota";
    }

    @RequestMapping("unalm")
    public String unalm() {

        return "unalm/unalm";
    }

    @RequestMapping("detalleNotasAcademicas")
    public String detalleNotasAcademicas(Model model,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        logger.debug("evaluacion {}", evaluacionId);
        Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);

        return "academico/docente/cargaacademica/detalleNotasAcademicas";
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
            PlanCalificacion planCalificacion,
            @RequestParam("cursoId") Long cursoId,
            @RequestParam("grupoId") Long grupoId,
            RedirectAttributes redirectAttr, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("Curso {}, Grupo {}", cursoId, grupoId);
            String message = "Aceptado correctamente.";

            cargaAcademicaService.aceptarPlanCalificacion(planCalificacion, cursoId, grupoId, ds);
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
            @RequestParam(name = "fechaEvaluacion", required = false) Date fechaEvaluacion,
            @RequestParam(name = "activacion", required = true) boolean activacion,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        logger.debug("activacion {}", activacion);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("evaluacion {}, Fecha evauacion {}", evaluacionId, fechaEvaluacion);
            Evaluacion evaluacion = null;
            if (fechaEvaluacion != null) {
                evaluacion = cargaAcademicaService.activarEvaluacion(evaluacionId, fechaEvaluacion, ds);
            } else {
                evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);
            }
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
    @RequestMapping("eliminarNotas")
    public JsonResponse eliminarNotas(
            Model model,
            @RequestParam(name = "evaluacion", required = true) Long evaluacionId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("evaluacion {}", evaluacionId);

            Evaluacion evaluacion = cargaAcademicaService.findEvaluacion(evaluacionId);

            List<MatriculaSeccion> matriculasSeccion = cargaAcademicaService.eliminarNotas(new Evaluacion(evaluacionId), ds);
            //    cargaAcademicaService.calcularNotasLista(matriculasSeccion, ds);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("evaSeleccionada", evaluacion.getTipoEvaluacion().getCodigo() + evaluacion.getNumero());
            node.put("evaId", evaluacion.getId());
            response.setMessage("Notas eliminadas correctamente.");
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
            List<MatriculaSeccion> matriculasSeccion = cargaAcademicaService.saveIngresoNotas(evaluacion, alumnoEvaluaciones, ds);
            //    cargaAcademicaService.calcularNotasLista(matriculasSeccion, ds);

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

                node.put("notaLetra", alumnoEvaluacion.getValorLetra());
            } else {
                node.put("nota", "");
                node.put("notaNumerica", "");

                node.put("notaLetra", "");
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

    @ResponseBody
    @RequestMapping("deletePlanCalifica")
    public JsonResponse deletePlanCalifica(
            @RequestParam("idPlanCalifica") Long idPlanCalifica,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            cargaAcademicaService.deletePlanCalificacion(idPlanCalifica, ds);

            response.setMessage("Plan de Calificacion eliminado satisfactoriamente");
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
    @RequestMapping("aceptarExpandir")
    public JsonResponse aceptarExpandir(
            @RequestBody EvaluacionExpandida[] evaluacionesExpandidas,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            cargaAcademicaService.saveAceptarExpandir(evaluacionesExpandidas);

            response.setMessage("Evaluaciones actualizadas.");
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
    @RequestMapping("cerrarActa")
    public JsonResponse cerrarActa(
            @RequestParam(name = "grupo", required = true) Long grupoId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        cargaAcademicaService.saveCerrarActa(new GrupoSeccion(grupoId), ds.getUsuario());
        String message = "Acta cerrada correctamente";
        response.setMessage(message);
        response.setSuccess(true);

        return response;
    }

    @ResponseBody
    @RequestMapping("desvincularPlanCalificacion")
    public JsonResponse desvincularPlanCalificacion(@RequestParam(name = "grupo", required = true) Long grupoId,
            HttpSession session, Model model,
            RedirectAttributes redirectAttr) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        //  ds.getDocente();
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        cargaAcademicaService.desvincularPlanCalificacion(new GrupoSeccion(grupoId));

        //     cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);
        // Notificaciones.crearMsg("Desvinculado satisfactoriamente", redirectAttr);
        response.setMessage("Desvinculado satisfactoriamente");
        response.setSuccess(true);
        return response;
    }

    @ResponseBody
    @RequestMapping("validarEvaluacionesIngresadas")
    public JsonResponse validarEvaluacionesIngresadas(@RequestParam(name = "evalExp", required = true) Long evalExpandidaId,
            HttpSession session, Model model,
            RedirectAttributes redirectAttr) {
        JsonResponse response = new JsonResponse();

        logger.debug("Evaluacion expandida " + evalExpandidaId);
        if (evalExpandidaId.equals(0L)) {
            response.setSuccess(true);
            return response;
        }
        List<AlumnoEvaluacion> alumnosEvaluaciones = cargaAcademicaService.allAlumnosEvaluacionesPorEvaluacionExpandida(evalExpandidaId);
        logger.debug("Alumnos Evaluaciones {}", alumnosEvaluaciones.size());
        response.setSuccess(true);
        if (!alumnosEvaluaciones.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("No se puede eliminar, La evaluación ya cuenta con notas ingresadas.");
        }
        return response;
    }
}
