package pe.edu.lamolina.pivot.controller.academico.acta;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/acta/acta")
public class ActaController {

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
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "app/academico/acta/acta";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        /*
        try {
            DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
            PeriodoLectivo periodo = ds.getPeriodo();

            List<PeriodoSede> sedesPeriodo = service.allSedesByPeriodo(filter, periodo, ds.getSedes());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (PeriodoSede sedePeriodo : sedesPeriodo) {
                VacanteGrado resumen = sedePeriodo.getResumenVacantes();
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", sedePeriodo.getId());
                node.put("idSede", sedePeriodo.getSede().getId());
                node.put("idPeriodo", sedePeriodo.getPeriodoLectivo().getId());
                node.put("year", sedePeriodo.getPeriodoLectivo().getYear());
                node.put("nombreSede", sedePeriodo.getSede().getNombre());
                node.put("estado", sedePeriodo.getEstado());
                node.put("estadoEnum", sedePeriodo.getEstadoEnum().getValue());
                node.put("matriculados", resumen.getMatriculados());
                node.put("vacantes", resumen.getVacantes());
                node.put("porc", resumen.getPorcentajeMatriculados());
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }
         */
        return json;
    }

}
