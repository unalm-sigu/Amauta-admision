package pe.edu.lamolina.pivot.controller.academico.plancurricular;

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
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/planCurricular")
public class PlanCurricularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlanCurricularService service;

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
        return "academico/plancurricular/planCurricular";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<PlanCurricular> curriculas = service.allByDynatable(filter, ds.getCarreras());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (PlanCurricular curricula : curriculas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", curricula.getId());
                node.put("cicloInicioVigencia", curricula.getCicloInicioVigencia().getDescripcion());
                node.put("estado", curricula.getEstado());
                node.put("fechaAprobacion", getFechaString(curricula.getFechaAprobado()));
                node.put("codigoCarrera", curricula.getCarrera().getCodigo());
                node.put("carrera", curricula.getCarrera().getNombre());
                node.put("tipoCarrera", curricula.getCarrera().getTipoEnum().getValue());
                node.put("orientacion", curricula.getOrientacionCarrera() == null ? null : curricula.getOrientacionCarrera().getNombre());
                node.put("facultad", curricula.getCarrera().getFacultad().getNombre());
                node.put("modalidad", curricula.getCarrera().getModalidadEstudio().getNombre());
                node.put("codigoModalidad", curricula.getCarrera().getModalidadEstudio().getCodigo());
                node.put("estado", curricula.getEstado());
                node.put("estadoEnum", curricula.getEstadoEnum().getValue());
                node.put("ciclos", curricula.getCiclos());

                node.put("cantObl", curricula.getCantidadCursosCurricula());
                node.put("cantOpc", curricula.getCantidadCursosOpcionales());
                node.put("cantAdc", curricula.getCantidadCursosAdicionales());
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

    private String getFechaString(Date fecha) {
        if (fecha == null) {
            return null;
        } else {
            return new DateTime(fecha).toString("dd/MM/yyyy");
        }
    }

    @ResponseBody
    @RequestMapping("cursosObligatorios")
    public DynatableResponse cursosObligatorios(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoCurricula> cursosCurricula = service.allCursosOblByDynatable(filter);

            Integer total = 0;
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoCurricula cursoCurricula : cursosCurricula) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Integer esRequisitoDe = cursoCurricula.getRequisitosCursoCurricula().size()
                        + cursoCurricula.getRequisitosCursoOpcional().size();

                node.put("id", cursoCurricula.getId());
                node.put("tipoCurso", cursoCurricula.getTipoCursoCurricula().getNombre());
                node.put("curso", cursoCurricula.getCurso().getNombre());
                node.put("numeroCiclo", cursoCurricula.getNumeroCiclo());
                node.put("numeroRomano", NumberFormat.roman(cursoCurricula.getNumeroCiclo()));
                node.put("codigo", cursoCurricula.getCurso().getCodigo());
                node.put("codigo2", cursoCurricula.getCurso().getCodigoAnterior1());
                node.put("creditos", cursoCurricula.getCreditos());
                node.put("creditosRequisito", cursoCurricula.getCreditosRequisito());
                node.put("cursosRequisito", cursoCurricula.getCursosCurricula().size());
                node.put("esRequisitoDe", esRequisitoDe);

                ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoCurricula> cursosRequisitos = cursoCurricula.getCursosCurricula();
                for (RequisitoCursoCurricula requisito : cursosRequisitos) {
                    ObjectNode nodeRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodeRequisito.put("curso", requisito.getCursoRequisito().getCurso().getNombre());
                    nodeRequisito.put("codigo", requisito.getCursoRequisito().getCurso().getCodigo());
                    nodeRequisito.put("codigo2", requisito.getCursoRequisito().getCurso().getCodigoAnterior1());
                    nodeRequisito.put("simultaneo", requisito.getSimultaneo());
                    nodeRequisito.put("tipoCurso", requisito.getCursoRequisito().getTipoCursoCurricula().getNombre());
                    nodeRequisito.put("numeroRomano", NumberFormat.roman(requisito.getCursoRequisito().getNumeroCiclo()));

                    arrayPreRequisitos.add(nodeRequisito);
                }
                node.set("prerrequisitos", arrayPreRequisitos);

                ArrayNode arrayPostRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoCurricula> cursosPostRequisitos = cursoCurricula.getRequisitosCursoCurricula();
                for (RequisitoCursoCurricula postrequisito : cursosPostRequisitos) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoCurricula().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoCurricula().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoCurricula().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoCurricula().getTipoCursoCurricula().getNombre());
                    nodePostRequisito.put("numeroRomano", NumberFormat.roman(postrequisito.getCursoCurricula().getNumeroCiclo()));

                    arrayPostRequisitos.add(nodePostRequisito);
                }
                node.set("postrrequisitos", arrayPostRequisitos);

                ArrayNode arrayPostRequisitosOpc = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosPostRequisitosOpc = cursoCurricula.getRequisitosCursoOpcional();
                for (RequisitoCursoOpcional postrequisito : cursosPostRequisitosOpc) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoOpcional().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoOpcional().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoOpcional().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoOpcional().getTipoCursoCurricula().getNombre());

                    arrayPostRequisitosOpc.add(nodePostRequisito);
                }
                node.set("postrrequisitosOpc", arrayPostRequisitosOpc);

                array.add(node);
                total += cursoCurricula.getCreditos();
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("curso", "TOTAL");
                node.put("creditos", total);
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

    @ResponseBody
    @RequestMapping("resumenCurricula")
    public DynatableResponse resumenCurricula(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            List<ResumenPlanCurricular> resumenes = service.allResPlanCurByDynatable(filter);

            Integer totalCreditos = 0, totalCursos = 0;
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ResumenPlanCurricular resumen : resumenes) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", resumen.getId());
                node.put("tipoCurso", resumen.getTipoCursoCurricula().getNombre());
                node.put("creditos", resumen.getCreditos());
                node.put("cursos", resumen.getCursos());

                array.add(node);
                totalCreditos += resumen.getCreditos();
                totalCursos += resumen.getCursos();
            }

            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("tipoCurso", "TOTAL");
                node.put("creditos", totalCreditos);
                node.put("cursos", totalCursos);
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

    @ResponseBody
    @RequestMapping("cursosAdicionales")
    public DynatableResponse cursosAdicionales(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoAdicionalCurricula> cursosAdicionales = service.allCursosAdcByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size cursso curricula {}", cursosAdicionales.size());

            for (CursoAdicionalCurricula cursoAdicional : cursosAdicionales) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoAdicional.getId());
                node.put("codigo", cursoAdicional.getCurso().getCodigo());
                node.put("codigo2", cursoAdicional.getCurso().getCodigoAnterior1());
                node.put("curso", cursoAdicional.getCurso().getNombre());
                node.put("creditos", cursoAdicional.getCurso().getCreditos());
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
    @RequestMapping("cursosElectivos")
    public DynatableResponse cursosElectivos(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<CursoOpcionalCurricula> cursosOpcionales = service.allCursosElecByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (CursoOpcionalCurricula cursoOpcional : cursosOpcionales) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoOpcional.getId());
                node.put("codigo", cursoOpcional.getCurso().getCodigo());
                node.put("codigo2", cursoOpcional.getCurso().getCodigoAnterior1());
                node.put("curso", cursoOpcional.getCurso().getNombre());
                node.put("creditos", cursoOpcional.getCurso().getCreditos());
                node.put("tipoCurso", cursoOpcional.getTipoCursoCurricula().getNombre());
                node.put("creditosRequisito", cursoOpcional.getCreditosRequisito());
                node.put("cursosRequisito", cursoOpcional.getCursosOpcionales().size());
                node.put("esRequisitoDe", cursoOpcional.getRequisitosCursoOpcionales().size());

                ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosRequisitos = cursoOpcional.getCursosOpcionales();
                for (RequisitoCursoOpcional requisito : cursosRequisitos) {
                    ObjectNode nodeRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodeRequisito.put("curso", requisito.getCursoRequisito().getNombre());
                    nodeRequisito.put("codigo", requisito.getCursoRequisito().getCodigo());
                    nodeRequisito.put("codigo2", requisito.getCursoRequisito().getCodigoAnterior1());
                    nodeRequisito.put("simultaneo", requisito.getSimultaneo());
                    nodeRequisito.put("tipoCurso", requisito.getTipoCursoCurricula().getNombre());
                    nodeRequisito.put("numeroRomano", requisito.getNumeroRomano());

                    arrayPreRequisitos.add(nodeRequisito);
                }
                node.set("prerrequisitos", arrayPreRequisitos);

                ArrayNode arrayPostRequisitos = new ArrayNode(JsonNodeFactory.instance);
                List<RequisitoCursoOpcional> cursosPostRequisitos = cursoOpcional.getRequisitosCursoOpcionales();
                for (RequisitoCursoOpcional postrequisito : cursosPostRequisitos) {
                    ObjectNode nodePostRequisito = new ObjectNode(JsonNodeFactory.instance);
                    nodePostRequisito.put("curso", postrequisito.getCursoOpcional().getCurso().getNombre());
                    nodePostRequisito.put("codigo", postrequisito.getCursoOpcional().getCurso().getCodigo());
                    nodePostRequisito.put("codigo2", postrequisito.getCursoOpcional().getCurso().getCodigoAnterior1());
                    nodePostRequisito.put("simultaneo", postrequisito.getSimultaneo());
                    nodePostRequisito.put("tipoCurso", postrequisito.getCursoOpcional().getTipoCursoCurricula().getNombre());

                    arrayPostRequisitos.add(nodePostRequisito);
                }
                node.set("postrrequisitos", arrayPostRequisitos);

                ArrayNode arrayPostRequisitosOpc = new ArrayNode(JsonNodeFactory.instance);
                node.set("postrrequisitosOpc", arrayPostRequisitosOpc);

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

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        PlanCurricular planCurricular = new PlanCurricular();
        planCurricular.init();

        List<CicloAcademico> ciclos = service.allUltimosCiclos(40);
        List<Carrera> carreras = service.allCarreras(ds.getCarreras());

        model.addAttribute("ciclos", ciclos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("carreras", carreras);

        return "academico/plancurricular/planCurricularForm";
    }

    @RequestMapping("{plancurricular}/addCursoObligatorio")
    public String addCursoObligatorio(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {

        List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurricula();
        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));
        CursoCurricula cursoCurricula = new CursoCurricula();
        cursoCurricula.setTipoCursoCurricula(new TipoCursoCurricula());
        cursoCurricula.setCursosCurricula(new ArrayList());
        cursoCurricula.setPlanCurricular(planCurricular);
        cursoCurricula.setCreditosRequisito(0);

        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        model.addAttribute("format", new NumberFormat());

        return "academico/plancurricular/agregarCursoObli";
    }

    @RequestMapping("{cursoCurricula}/editarCursoObligatorio")
    public String editarCursoObligatorio(@PathVariable("cursoCurricula") Long cursoCurriculaId, Model model, HttpSession session) {
        CursoCurricula cursoCurricula = service.findCursoCurricula(cursoCurriculaId);

        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("format", new NumberFormat());
        return "academico/plancurricular/agregarCursoObli";
    }

    @RequestMapping("{plancurricular}/agregarCursoElectivo")
    public String agregarCursoElectivo(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {
        List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurriculasElectivos();
        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));

        CursoOpcionalCurricula cursoOpcional = new CursoOpcionalCurricula();
        cursoOpcional.setCreditosRequisito(0);
        cursoOpcional.setPlanCurricular(planCurricular);
        cursoOpcional.setCursosOpcionales(new ArrayList());

        model.addAttribute("cursoOpcional", cursoOpcional);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        return "academico/plancurricular/agregarCursoElec";
    }

    @RequestMapping("{cursoElectivo}/editarCursoElectivo")
    public String editarCursoElectivo(@PathVariable("cursoElectivo") Long cursoElectivoId, Model model, HttpSession session) {
        CursoOpcionalCurricula cursoElectivo = service.findCursoElectivo(cursoElectivoId);

        model.addAttribute("cursoOpcional", cursoElectivo);
        model.addAttribute("planCurricular", cursoElectivo.getPlanCurricular());
        model.addAttribute("format", new NumberFormat());
        return "academico/plancurricular/agregarCursoElec";
    }

    @RequestMapping("{plancurricular}/agregarCursoAdicional")
    public String agregarCursoAdicional(@PathVariable("plancurricular") Long plancurricularId, Model model, HttpSession session) {
        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(plancurricularId));
        model.addAttribute("planCurricular", planCurricular);
        return "academico/plancurricular/agregarCursoAdc";
    }

    @ResponseBody
    @RequestMapping("{carrera}/orientacionCarrera")
    public String orientacionCarrera(@PathVariable("carrera") Long carrera, HttpSession session) {
        List<OrientacionCarrera> orientaciones = service.allOrientacionByCarreraEstado(new Carrera(carrera), EstadoEnum.ACT);

        String template = "<option value=\"%s\">%s</option>";
        StringBuilder options = new StringBuilder();

        if (orientaciones.isEmpty()) {
            return "";
        }

        options.append(String.format(template, "", ""));
        for (OrientacionCarrera orientacion : orientaciones) {
            options.append(String.format(template, orientacion.getId().toString(), orientacion.getNombre()));
        }

        return options.toString();
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cambiarTipoCursoCurricula")
    public JsonResponse cambiarTipoCursoCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurriculaId, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            TipoCursoCurricula tipoCursoCurricula = service.findTipoCurricula(tipoCursoCurriculaId);
            List<Curso> cursos = service.allCursosByCodigo(tipoCursoCurricula.getCodigo());

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("tieneRequisitos", tipoCursoCurricula.isTieneRequisitos());
            node.put("tieneCreditoManual", tipoCursoCurricula.isTieneCreditoManual());
            if (cursos != null && !cursos.isEmpty()) {
                ObjectNode nodeCurso = new ObjectNode(JsonNodeFactory.instance);
                nodeCurso.put("id", cursos.get(0).getId());
                nodeCurso.put("codigo", cursos.get(0).getCodigo());
                nodeCurso.put("curso", cursos.get(0).getNombre());
                node.putPOJO("cursoDefault", nodeCurso);
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
    @RequestMapping("savePlanCurricular")
    public JsonResponse savePlanCurricular(PlanCurricular planCurricular, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String message = "Creado exitosamente.";

            if (planCurricular.getId() == null) {
                service.savePlanCurricular(planCurricular);
                node.put("operation", "s");
                node.put("planCurricular", planCurricular.getId());

            } else {
                node.put("operation", "u");
                message = "Actualizado exitosamente.";
                service.updatePlanCurricular(planCurricular);
            }

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
    @RequestMapping("saveCursoObligatorio")
    public JsonResponse saveCursoObligatorio(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            if (cursoCurricula.getId() == null) {
                service.saveCursoCurricula(cursoCurricula, ds);
            } else {
                message = "Curso actualizado exitosamente.";
                service.updateCursoCurricula(cursoCurricula, ds);
            }

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
    @RequestMapping("deleteCursoObligatorio")
    public JsonResponse deleteCursoObligatorio(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deleteCursoCurricula(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso eliminado exitosamente");

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
    @RequestMapping("trasladarCiclo")
    public JsonResponse trasladarCiclo(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarCiclo(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso trasladado a otro ciclo exitosamente");

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
    @RequestMapping("trasladarToElectivos")
    public JsonResponse trasladarToElectivos(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarToElectivos(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso traslado al grupo de electivos exitosamente");

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
    @RequestMapping("trasladarToObligatorios")
    public JsonResponse trasladarToObligatorios(CursoCurricula cursoCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladarToObligatorios(cursoCurricula, ds);

            response.setSuccess(true);
            response.setMessage("Curso traslado al grupo de Obligatorios/Generales exitosamente");

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
    @RequestMapping("saveCursoAdicional")
    public JsonResponse saveCursoAdicional(CursoAdicionalCurricula cursoAdicionalCurricula, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            service.saveCursoAdicional(cursoAdicionalCurricula, ds);

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
    @RequestMapping("saveCursoElectivo")
    public JsonResponse saveCursoElectivo(CursoOpcionalCurricula cursoOpcional, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            String message = "Curso agregado exitosamente.";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            if (cursoOpcional.getId() == null) {
                service.saveCursoOpcional(cursoOpcional, ds);
            } else {
                message = "Curso actualizado exitosamente.";
                service.updateCursoOpcional(cursoOpcional, ds);
            }

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

    @RequestMapping("{planCurricular}/succesSave")
    public String succesSave(@PathVariable("planCurricular") Long planCurricularId, RedirectAttributes redirectAttr, HttpSession session) {
        Notificaciones.crearMsg(Messages.CREATED, redirectAttr);
        return "redirect:/academico/planCurricular/" + planCurricularId + "/editarPlanCurricular";
    }

    @RequestMapping("{planCurricular}/editarPlanCurricular")
    public String editarPlanCurricular(@PathVariable("planCurricular") Long planCurricularId, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        PlanCurricular planCurricular = service.findPlanCurricularById(new PlanCurricular(planCurricularId));
        Carrera carrera = planCurricular.getCarrera();
        List<CicloAcademico> ciclos = service.allUltimosCiclos(40);
        List<OrientacionCarrera> orientaciones = service.allOrientacionByCarreraEstado(carrera, EstadoEnum.ACT);
        List<TipoCursoCurricula> tiposCursoCurriculas = service.allTiposCursoCurriculasElectivos();
        List<TipoCursoCurricula> tiposCursoCurriculasObli = service.allTiposCursoCurriculasObligatorios();

        model.addAttribute("ciclos", ciclos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("orientaciones", orientaciones);
        model.addAttribute("format", new NumberFormat());
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        model.addAttribute("tiposCursoCurriculasObli", tiposCursoCurriculasObli);

        return "academico/plancurricular/planCurricularForm";
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cursosCurricula")
    public JsonResponse cursosCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurricula, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            List<CursoCurricula> cursosCurricula = service.allCursosCurriculaByFilter(new TipoCursoCurricula(tipoCursoCurricula));
            String template = "<option value=\"%d\">%s<option>";
            StringBuilder select = new StringBuilder();
            if (!cursosCurricula.isEmpty()) {
                for (CursoCurricula cursoCurriculaEach : cursosCurricula) {
                    select.append(String.format(template, cursoCurriculaEach.getId(), cursoCurriculaEach.getCurso().getNombre()));
                }
            }

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("cursosCurricula", select.toString());
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
    @RequestMapping("buscarCursos")
    public JsonResponse buscarCursos(
            @RequestParam("nombre") String nombre,
            @RequestParam(name = "tipoCurricula", required = false) String tipoCurricula,
            @RequestParam(name = "tipoCursoCurricula", required = false) Long idTipoCursoCurricula, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<TipoCurriculaEnum> tiposCurricula = null;
            TipoCursoCurricula tipoCursoCurricula = null;

            if (tipoCurricula != null) {
                tiposCurricula = new ArrayList();
                tiposCurricula.add(TipoCurriculaEnum.valueOf(tipoCurricula));
            }
            if (idTipoCursoCurricula != null) {
                tipoCursoCurricula = service.findTipoCurricula(idTipoCursoCurricula);
                tiposCurricula = tipoCursoCurricula.getTiposCursoCurricula();
            }

            List<Curso> cursos = service.allCursoByNombreTipoCurricula(nombre, tiposCurricula);

            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                if (tipoCursoCurricula != null && tipoCursoCurricula.isTieneCreditoManual()) {
                    if (!curso.getCodigo().equals(tipoCursoCurricula.getCodigo())) {
                        continue;
                    }
                }
                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.nombre"));
                json.put("facultad", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.facultad.nombre"));
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("buscarCursosCurricula")
    public JsonResponse buscarCursosCurricula(CursoCurricula cursoCurriculaForm, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<CursoCurricula> cursosCurricula = service.allCursoCurriculaByNombre(cursoCurriculaForm);

            for (CursoCurricula cursoCurricula : cursosCurricula) {
                ObjectNode json = new ObjectNode(jsonFactory);
                Curso curso = cursoCurricula.getCurso();

                json.put("id", cursoCurricula.getId());
                json.put("numeroCiclo", cursoCurricula.getNumeroCiclo());
                json.put("numeroRomano", NumberFormat.roman(cursoCurricula.getNumeroCiclo()));
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("codigo2", curso.getCodigoAnterior1());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", curso.getDepartamentoAcademico().getNombre());
                json.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));

                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("buscarCursosOpcionales")
    public JsonResponse buscarCursosOpcionales(CursoCurricula cursoCurriculaForm, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            cursoCurriculaForm.setNumeroCiclo(2000);
            List<RequisitoCursoOpcional> cursosComodines = service.allCursosObligatoriosAndElectivosByNombre(cursoCurriculaForm);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (RequisitoCursoOpcional cursoComodin : cursosComodines) {
                ObjectNode json = new ObjectNode(jsonFactory);
                Curso curso = cursoComodin.getCursoRequisito();

                json.put("id", RandomStringUtils.randomNumeric(4));
                json.put("idCursoCurricula", (Long) ObjectUtil.getParentTree(cursoComodin, "cursoRequisitoCurricula.id"));
                json.put("idCursoOpcional", (Long) ObjectUtil.getParentTree(cursoComodin, "cursoRequisitoOpcional.id"));
                json.put("numeroCiclo", cursoComodin.getNumeroCiclo());
                json.put("numeroRomano", cursoComodin.getNumeroRomano());
                json.put("tipoCursoCurricula", cursoComodin.getTipoCursoCurricula().getNombre());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("codigo2", curso.getCodigoAnterior1());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", curso.getDepartamentoAcademico().getNombre());
                json.put("facultad", curso.getDepartamentoAcademico().getFacultad().getNombre());
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));

                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteCursoAdicional")
    public JsonResponse deleteCursoAdicional(@RequestParam("id") Long cursoAdicionalId, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            service.deleteCursoAdicional(cursoAdicionalId);

            response.setData(node);
            response.setMessage("Curso adicional eliminado.");
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
    @RequestMapping("deleteCursoElectivo")
    public JsonResponse deleteCursoElectivo(CursoOpcionalCurricula cursoElectivo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            service.deleteCursoOpcional(cursoElectivo);

            response.setData(node);
            response.setMessage("Curso electivo eliminado.");
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
    @RequestMapping("cursoPorTipoCurricula")
    public JsonResponse cursoPorTipoCurricula(@RequestParam("tipoCurricula") String tipoCurricula, Model model, HttpSession session) {

        List<TipoCurriculaEnum> tiposCurricula = new ArrayList<>();
        tiposCurricula.add(TipoCurriculaEnum.ADIC);
        List<Curso> cursos = service.allCursoByNombreTipoCurricula(null, tiposCurricula);

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        if (cursos != null && !cursos.isEmpty()) {
            node.put("id", cursos.get(0).getId());
            node.put("codigo", cursos.get(0).getCodigo());
            node.put("curso", cursos.get(0).getNombre());
            response.setData(node);
            response.setSuccess(Boolean.TRUE);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("eliminarPlan")
    public JsonResponse eliminarPlan(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.deletePlanCurricular(plan);

            response.setMessage("Plan curricular eliminado satisfactoriamente");
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
    @RequestMapping("desactivarPlan")
    public JsonResponse desactivarPlan(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.desactivarPlanCurricular(plan);

            response.setMessage("Plan curricular eliminado satisfactoriamente");
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
    @RequestMapping("clonarPlan")
    public JsonResponse clonarPlan(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            PlanCurricular planBD = service.clonarPlanCurricular(plan, ds.getCicloAcademico(), ds);

            response.setData(planBD.getId());
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
    @RequestMapping("dataCurricula")
    public JsonResponse dataCurricula(PlanCurricular plan, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            PlanCurricular planBD = service.findPlanCurricularById(plan);

            List<CursoCurricula> cursosCurr = planBD.getCursoCurricula();
            Map<Integer, List<CursoCurricula>> mapCursosCurr = TypesUtil.convertListToMapList("numeroCiclo", cursosCurr);

            for (Map.Entry<Integer, List<CursoCurricula>> entry : mapCursosCurr.entrySet()) {
                Integer nroCiclo = entry.getKey();
                if (nroCiclo == 0) {
                    continue;
                }

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("numeroCiclo", nroCiclo);
                node.put("numeroRomano", NumberFormat.roman(nroCiclo));

                ArrayNode arrayCursos = new ArrayNode(JsonNodeFactory.instance);
                List<CursoCurricula> cursosCiclo = entry.getValue();
                for (CursoCurricula cursoCurr : cursosCiclo) {
                    Curso curso = cursoCurr.getCurso();
                    ObjectNode nodeCurso = new ObjectNode(JsonNodeFactory.instance);
                    nodeCurso.put("id", cursoCurr.getId());
                    nodeCurso.put("tipo", cursoCurr.getTipoCursoCurricula().getCodigo());
                    nodeCurso.put("curso", curso.getNombre());
                    nodeCurso.put("codigo", curso.getCodigo());
                    nodeCurso.put("creditos", cursoCurr.getCreditos());
                    nodeCurso.put("numeroCurso", cursoCurr.getNumeroCurso());
                    nodeCurso.put("creditosRequisito", cursoCurr.getCreditosRequisito());

                    ArrayNode arrayRequisitos = new ArrayNode(JsonNodeFactory.instance);
                    List<RequisitoCursoCurricula> requisitos = cursoCurr.getRequisitosCursoCurricula();
                    for (RequisitoCursoCurricula requisito : requisitos) {
                        CursoCurricula cursoReq = requisito.getCursoRequisito();
                        ObjectNode nodeReq = new ObjectNode(JsonNodeFactory.instance);
                        nodeReq.put("idReq", cursoReq.getId());
                        nodeReq.put("simultaneo", requisito.getSimultaneo());
                        arrayRequisitos.add(nodeReq);
                    }

                    nodeCurso.set("requisitos", arrayRequisitos);
                    arrayCursos.add(nodeCurso);
                }

                node.set("cursos", arrayCursos);
                array.add(node);
            }

            response.setData(array);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("moveCurso")
    public JsonResponse moveCurso(
            CursoCurricula cursoCurricula,
            @RequestParam("direccion") String direccion, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.moveCurso(cursoCurricula, direccion, ds);

            response.setSuccess(true);
            response.setMessage("El curso se ha movido satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
