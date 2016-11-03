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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;
import pe.edu.lamolina.pivot.model.academico.EvaluacionPlan;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.academico.SistemaNotas;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
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
        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 5);
                node.put("codigo", "SC-003");
                node.put("formula", "EP(20) EF(25) 5PC(40) 3TA(15)");
                node.put("origen", "Dpto. Académico");
                node.put("estado", "ACT");
                node.put("estadoEnum", "Activo");
                array.add(node);
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 34);
                node.put("codigo", "SC-103");
                node.put("formula", "EP(20) EF(25) 5PC(40) 3TA(15)");
                node.put("origen", "Docente");
                node.put("estado", "CRE");
                node.put("estadoEnum", "Creado");
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

        return "app/academico/systemcalifica/sistema/detalleSistema";
    }

    @RequestMapping("{sistema}/cursos")
    public String cursos(@PathVariable("sistema") Long idSistema, Model model, HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

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
        model.addAttribute("tipoEvaluaciones", sistemaService.allTipoEvaluacion());
        model.addAttribute("sistemasNotas", sistemaService.allSistemasNotas());
        model.addAttribute("tiposSeccion", TipoSeccionEnum.values());
        return "app/academico/systemcalifica/sistema/nuevoSistema";
    }

    @RequestMapping("save")
    public String save(PlanCalificacion planCalificacion,
            RedirectAttributes redirectAttr, HttpSession session) {
        logger.debug("Plan Califica {}", planCalificacion.toString());
        logger.debug("Planes de evaluacion {}", planCalificacion.getEvaluacionPlan().size());

        return "redirect:/academico/systemcalifica/sistema/sistema";
    }
}
