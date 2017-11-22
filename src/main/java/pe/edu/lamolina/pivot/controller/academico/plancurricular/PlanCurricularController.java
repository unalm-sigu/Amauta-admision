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
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoCurricula;
import pe.edu.lamolina.pivot.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.pivot.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCurriculaEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/planCurricular/plan")
public class PlanCurricularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlanCurricularService planCurricularService;

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

        return "academico/plancurricular/plan/planCurricular";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();

            List<PlanCurricular> listaPlanes = planCurricularService.allByDynatable(filter, ds.getCarreras());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size planes {}", listaPlanes.size());

            for (PlanCurricular planCurEach : listaPlanes) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", planCurEach.getId());
                node.put("cicloInicioVig", planCurEach.getCicloInicioVigencia().getDescripcion());
                node.put("estado", planCurEach.getEstado());
                node.put("fechaAprobacion", new DateTime(planCurEach.getFechaAprobado()).toString("dd/MM/yyyy"));
                node.put("carreraCodigo", planCurEach.getCarrera().getCodigo());
                node.put("carreraNombre", planCurEach.getCarrera().getNombre());

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
    @RequestMapping("listCurObl")
    public DynatableResponse listCurObl(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();
            List<CursoCurricula> listaCursoCurricula = planCurricularService.allCursosOblByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size cursso curricula {}", listaCursoCurricula.size());

            for (CursoCurricula cursoCurEach : listaCursoCurricula) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", cursoCurEach.getId());
                node.put("tipoCursoCurr", cursoCurEach.getTipoCursoCurricula().getNombre());
                node.put("cursoNombre", cursoCurEach.getCurso().getNombre());
                node.put("cursoCurrCredito", cursoCurEach.getCreditos());

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
    @RequestMapping("listCurResumen")
    public DynatableResponse listCurResumen(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();
            List<ResumenPlanCurricular> listaResPlanCur = planCurricularService.allResPlanCurByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size resumen curricula {}", listaResPlanCur.size());

            for (ResumenPlanCurricular resPlanCur : listaResPlanCur) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", resPlanCur.getId());
                node.put("tipoCursoCurr", resPlanCur.getTipoCursoCurricula().getNombre());

                node.put("cursoCurrCredito", resPlanCur.getCreditos());

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
    @RequestMapping("listCurAdc")
    public DynatableResponse listCurAdc(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();
            List<CursoAdicionalCurricula> listaCursoCurricula = planCurricularService.allCursosAdcByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size cursso curricula {}", listaCursoCurricula.size());

            for (CursoAdicionalCurricula cursoCurEach : listaCursoCurricula) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("cCurriculaAdcId", cursoCurEach.getId());
                node.put("cursoCodigo", cursoCurEach.getCurso().getCodigo());
                node.put("cursoNombre", cursoCurEach.getCurso().getNombre());
                node.put("cursoCreditos", cursoCurEach.getCurso().getCreditos());
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
    @RequestMapping("listCurElec")
    public DynatableResponse listCurElec(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {
            DepartamentoAcademico dpto = ds.getDepartamentoAcademico();
            List<CursoOpcionalCurricula> listaCursoCurricula = planCurricularService.allCursosElecByDynatable(filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            logger.debug("size cursso curricula {}", listaCursoCurricula.size());

            for (CursoOpcionalCurricula cursoCurEach : listaCursoCurricula) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("cCurriculaOpcId", cursoCurEach.getId());
                node.put("cursoCodigo", cursoCurEach.getCurso().getCodigo());
                node.put("cursoNombre", cursoCurEach.getCurso().getNombre());
                node.put("cursoCreditos", cursoCurEach.getCurso().getCreditos());
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

        DateTime today = new DateTime();
        DepartamentoAcademico departamentoAcademico = ds.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();

        PlanCurricular planCurricular = new PlanCurricular();
        planCurricular.init();

        List<CicloAcademico> ciclosAcademicos = planCurricularService.allRecientesCiclosAcad(today.getYear() - 2, 10);
        List<Carrera> carreras = planCurricularService.allCarrerasByFilter(facultad, EstadoEnum.ACT);

        model.addAttribute("ciclosAcademicos", ciclosAcademicos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("carrerasFacultad", ds.getCarreras());

        return "academico/plancurricular/plan/nuevoPlanCurricular";
    }

    @RequestMapping("{plancurricular}/agregarCursoOblgPlan")
    public String agregarCursoOblgPlan(
            @PathVariable("plancurricular") Long plancurricularId,
            Model model, HttpSession session) {

        List<TipoCursoCurricula> tiposCursoCurriculas = planCurricularService.allTiposCursoCurricula();
        PlanCurricular planCurricular = planCurricularService.findPlanCurricularById(new PlanCurricular(plancurricularId));
        CursoCurricula cursoCurricula = new CursoCurricula();
        cursoCurricula.setTipoCursoCurricula(new TipoCursoCurricula());
        cursoCurricula.setPlanCurricular(planCurricular);
        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        return "academico/plancurricular/plan/agregarCurso";
    }

    @RequestMapping("{plancurricular}/agregarCursoElecPlan")
    public String agregarCursoElecPlan(
            @PathVariable("plancurricular") Long plancurricularId,
            Model model, HttpSession session) {
        List<TipoCursoCurricula> tiposCursoCurriculas = planCurricularService.allTiposCursoCurricula();
        List<TipoCursoCurricula> tiposCursoCurriculasAlt = new ArrayList();
        PlanCurricular planCurricular = planCurricularService.findPlanCurricularById(new PlanCurricular(plancurricularId));

        for (TipoCursoCurricula tiposCursoCurricula : tiposCursoCurriculas) {

            if (tiposCursoCurricula.getCodigo().equals(TipoCursoCurriculaEnum.ELC.name())
                    || tiposCursoCurricula.getCodigo().equals(TipoCursoCurriculaEnum.ELE.name())
                    || tiposCursoCurricula.getCodigo().equals(TipoCursoCurriculaEnum.ELF.name())) {
                tiposCursoCurriculasAlt.add(tiposCursoCurricula);
            }
        }

        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculasAlt);
        return "academico/plancurricular/plan/agregarCursoElec";
    }

    @RequestMapping("{plancurricular}/agregarCursoAdcPlan")
    public String agregarCursoAdcPlan(
            @PathVariable("plancurricular") Long plancurricularId,
            Model model, HttpSession session) {
        PlanCurricular planCurricular = planCurricularService.findPlanCurricularById(new PlanCurricular(plancurricularId));
        model.addAttribute("planCurricular", planCurricular);
        return "academico/plancurricular/plan/agregarCursoAdc";
    }

    @ResponseBody
    @RequestMapping("{carrera}/orientacionCarrera")
    public String orientacionCarrera(@PathVariable("carrera") Long carrera,
            Model model, HttpSession session) {
        List<OrientacionCarrera> orientacionesCarrera = planCurricularService.allOrientacionCarreraByFilter(new Carrera(carrera), EstadoEnum.ANU);
        String template = "<option value=\"%d\">%s<option>";
        StringBuilder select = new StringBuilder();
        if (!orientacionesCarrera.isEmpty()) {
            for (OrientacionCarrera orientacionCarrera : orientacionesCarrera) {
                select.append(String.format(template, orientacionCarrera.getId(), orientacionCarrera.getNombre()));
            }
        }
        return select.toString();
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cambiarTipoCursoCurricula")
    public JsonResponse cambiarTipoCursoCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurriculaId,
            Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            TipoCursoCurricula tipoCursoCurricula = planCurricularService.findTipoCurricula(tipoCursoCurriculaId);

            List<Curso> cursos = planCurricularService.allCursosByCodigo(tipoCursoCurricula.getCodigo());

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("tieneRequisitos", tipoCursoCurricula.isTieneRequisitos());
            node.put("tieneCreditoManual", tipoCursoCurricula.isTieneCreditoManual());
            if (cursos != null && !cursos.isEmpty()) {
                ObjectNode nodeCur = new ObjectNode(JsonNodeFactory.instance);
                nodeCur.put("id", cursos.get(0).getId());
                nodeCur.put("cursoCodigo", cursos.get(0).getCodigo());
                nodeCur.put("cursoNombre", cursos.get(0).getNombre());
                node.putPOJO("cursoDefault", nodeCur);
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
    public JsonResponse savePlanCurricular(
            @ModelAttribute("planCurricular") PlanCurricular planCurricular,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            if (planCurricular.getId() == null) {
                planCurricularService.savePlanCurricular(planCurricular);
                //    node.put("operation", "s");
                node.put("planCurricular", planCurricular.getId());
                message = "Creado exitosamente.";
            } else {
                node.put("operation", "u");
                message = "Actualizado exitosamente.";
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
    @RequestMapping("saveAgregarCursoObl")
    public JsonResponse saveAgregarCursoObl(
            @ModelAttribute("cursoCurricula") CursoCurricula cursoCurricula,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            if (cursoCurricula.getId() == null) {
                message = "Curso agregado exitosamente.";
                planCurricularService.agregarCursoCurricula(cursoCurricula);
            } else {
                message = "Curso actualizado exitosamente.";
                planCurricularService.updateCursoCurricula(cursoCurricula);
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
    @RequestMapping("saveAgregarCursoAdc")
    public JsonResponse saveAgregarCursoAdc(
            @ModelAttribute("cursoAdicionalCurricula") CursoAdicionalCurricula cursoAdicionalCurricula,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            message = "Curso agregado exitosamente.";
            planCurricularService.agregarCursoAdcCurricula(cursoAdicionalCurricula);

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
    @RequestMapping("saveAgregarCursoEle")
    public JsonResponse saveAgregarCursoEle(
            @ModelAttribute("cursoAdicionalCurricula") CursoOpcionalCurricula cursoOpcionalCurricula,
            RedirectAttributes redirectAttr,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            String message = "";
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

            message = "Curso agregado exitosamente.";
            planCurricularService.agregarCursoOpcCurricula(cursoOpcionalCurricula);

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
    public String succesSave(@PathVariable("planCurricular") Long planCurricularId,
            RedirectAttributes redirectAttr,
            Model model, HttpSession session) {
        Notificaciones.crearMsg(Messages.CREATED, redirectAttr);
        return "redirect:/academico/planCurricular/plan/" + planCurricularId + "/editarPlanCurricular";
    }

    @RequestMapping("{planCurricular}/editarPlanCurricular")
    public String editarPlanCurricular(@PathVariable("planCurricular") Long planCurricularId,
            RedirectAttributes redirectAttr,
            Model model, HttpSession session) {
        logger.debug("entro a nuevo");

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DepartamentoAcademico departamentoAcademico = ds.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();
        DateTime today = new DateTime();

        PlanCurricular planCurricular = planCurricularService.findPlanCurricularById(new PlanCurricular(planCurricularId));
        List<CicloAcademico> ciclosAcademicos = planCurricularService.allRecientesCiclosAcad(today.getYear() - 2, 10);
        List<Carrera> carreras = planCurricularService.allCarrerasByFilter(facultad, EstadoEnum.ACT);

        model.addAttribute("ciclosAcademicos", ciclosAcademicos);
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("carrerasFacultad", carreras);

        return "academico/plancurricular/plan/nuevoPlanCurricular";
    }

    @ResponseBody
    @RequestMapping("{tipoCursoCurricula}/cursosCurricula")
    public JsonResponse cursosCurricula(@PathVariable("tipoCursoCurricula") Long tipoCursoCurricula,
            Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<CursoCurricula> cursosCurricula = planCurricularService.allCursosCurriculaByFilter(new TipoCursoCurricula(tipoCursoCurricula));
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
            // response.setMessage(message);

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
            @RequestParam(name = "tipoCursoCurricula", required = false) Long tipoCursoCurriculaId,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<TipoCurriculaEnum> tiposCurricula = null;
            TipoCursoCurricula tipoCursoCurricula = null;

            if (tipoCurricula != null) {
                tiposCurricula = new ArrayList<>();
                tiposCurricula.add(TipoCurriculaEnum.valueOf(tipoCurricula));
            }
            if (tipoCursoCurriculaId != null) {
                tipoCursoCurricula = planCurricularService.findTipoCurricula(tipoCursoCurriculaId);
                tiposCurricula = tipoCursoCurricula.getTiposCursoCurricula();
            }

            List<Curso> cursos = planCurricularService.allCursoByNombreTipoCurricula(nombre, tiposCurricula);

            for (Curso cur : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                if (tipoCursoCurricula != null && tipoCursoCurricula.isTieneCreditoManual()) {
                    if (!cur.getCodigo().equals(tipoCursoCurricula.getCodigo())) {
                        continue;
                    }
                }
                json.put("id", cur.getId());
                json.put("cursoNombre", cur.getCodigo());
                json.put("cursoCodigo", cur.getNombre());
                json.put("cursoTpc", cur.getTpc());
                json.put("cursoCreditos", cur.getCreditos());
                json.put("departamentoNombre", ObjectUtil.getParentTree(cur, "departamentoAcademico.nombre") != null ? cur.getDepartamentoAcademico().getNombre() : "");
                json.put("facultadNombre", ObjectUtil.getParentTree(cur, "departamentoAcademico.facultad.nombre") != null ? cur.getDepartamentoAcademico().getFacultad().getNombre() : "");
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
    public JsonResponse buscarCursosCurricula(
            @RequestParam("nombre") String nombre,
            @RequestParam("planCurricular") Long planCurricular,
            @RequestParam("numeroCiclo") Integer numeroCiclo,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<CursoCurricula> cursosCurricula = planCurricularService.allCursoCurriculaByNombre(planCurricular, numeroCiclo, nombre);

            for (CursoCurricula cur : cursosCurricula) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", cur.getCurso().getId());
                json.put("cursoNombre", cur.getCurso().getCodigo());
                json.put("cursoCodigo", cur.getCurso().getNombre());
                json.put("cursoTpc", cur.getCurso().getTpc());
                json.put("cursoCreditos", cur.getCreditos());
                json.put("departamentoNombre", cur.getCurso().getDepartamentoAcademico().getNombre());
                json.put("facultadNombre", cur.getCurso().getDepartamentoAcademico().getFacultad().getNombre());
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
    @RequestMapping("incluirCursoReq")
    public JsonResponse incluirCursoReq(@RequestParam("cursoCurriculaReq") Long cursoCurriculaReqId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            CursoCurricula cursoCurricula = planCurricularService.findCursoCurricula(cursoCurriculaReqId);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("cCurriculaId", cursoCurricula.getId());
            node.put("cCurriculaNumeroCiclo", cursoCurricula.getNumeroCiclo());
            node.put("cCurriculaCreditos", cursoCurricula.getCreditos());
            node.put("cursoNombre", cursoCurricula.getCurso().getNombre());
            node.put("cursoCodigo", cursoCurricula.getCurso().getCodigo());
            response.setData(node);
            response.setMessage("Curso asignado.");
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
    @RequestMapping("deleteCurAdi")
    public JsonResponse deleteCurAdi(@RequestParam("cursoCurriculaReq") Long cursoAdicionalId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            planCurricularService.deleteCursoAdicional(cursoAdicionalId);
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
    @RequestMapping("deleteCurElec")
    public JsonResponse deleteCurElec(@RequestParam("cCurriculaOpcId") Long cCurriculaOpcId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            planCurricularService.deleteCursoOpcional(cCurriculaOpcId);
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

    @RequestMapping("{cursoCurricula}/editarCursoOblgPlan")
    public String editarCursoOblgPlan(
            @PathVariable("cursoCurricula") Long cursoCurriculaId,
            Model model, HttpSession session) {

        CursoCurricula cursoCurricula = planCurricularService.findCursoCurricula(cursoCurriculaId);
        List<TipoCursoCurricula> tiposCursoCurriculas = planCurricularService.allTiposCursoCurricula();
        //    PlanCurricular planCurricular = planCurricularService.findPlanCurricularById(new PlanCurricular(plancurricularId));
        model.addAttribute("cursoCurricula", cursoCurricula);
        model.addAttribute("planCurricular", cursoCurricula.getPlanCurricular());
        model.addAttribute("tiposCursoCurriculas", tiposCursoCurriculas);
        return "academico/plancurricular/plan/agregarCurso";
    }

    @ResponseBody
    @RequestMapping("cursoPorTipoCurricula")
    public JsonResponse cursoPorTipoCurricula(
            @RequestParam("tipoCurricula") String tipoCurricula,
            Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        List<TipoCurriculaEnum> tiposCurricula = new ArrayList<>();
        tiposCurricula.add(TipoCurriculaEnum.ADIC);
        List<Curso> cursos = planCurricularService.allCursoByNombreTipoCurricula(null, tiposCurricula);

        response.setSuccess(Boolean.FALSE);
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        if (cursos != null && !cursos.isEmpty()) {
            node.put("id", cursos.get(0).getId());
            node.put("cursoCodigo", cursos.get(0).getCodigo());
            node.put("cursoNombre", cursos.get(0).getNombre());
            response.setData(node);
            response.setSuccess(Boolean.TRUE);
        }
        return response;
    }

}
