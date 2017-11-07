package pe.edu.lamolina.pivot.controller.academico.plancurricular;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
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
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        logger.debug("entro a nuevo");

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        DepartamentoAcademico departamentoAcademico = ds.getDepartamentoAcademico();
        Facultad facultad = departamentoAcademico.getFacultad();

        PlanCurricular planCurricular = new PlanCurricular();
        model.addAttribute("planCurricular", planCurricular);
        model.addAttribute("carrerasFacultad", planCurricularService.allCarrerasByFilter(facultad, EstadoEnum.ACT));
        return "academico/plancurricular/plan/nuevoPlanCurricular";
    }

    @RequestMapping("{plancurricular}/agregarCursoOblgPlan")
    public String agregarCursoOblgPlan(
            @PathVariable("plancurricular") Long plancurricular,
            Model model, HttpSession session) {
        model.addAttribute("planCurricular", new PlanCurricular());
        return "academico/plancurricular/plan/agregarCurso";
    }

    @RequestMapping("{plancurricular}/agregarCursoElecPlan")
    public String agregarCursoElecPlan(
            @PathVariable("plancurricular") Long plancurricular,
            Model model, HttpSession session) {
        model.addAttribute("planCurricular", new PlanCurricular());
        return "academico/plancurricular/plan/agregarCursoElec";
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

}
