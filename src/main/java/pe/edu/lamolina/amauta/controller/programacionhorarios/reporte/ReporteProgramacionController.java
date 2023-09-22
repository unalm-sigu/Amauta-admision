package pe.edu.lamolina.amauta.controller.programacionhorarios.reporte;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Slf4j
@Controller
@RequestMapping("reporte/programacion")
public class ReporteProgramacionController {

    @Autowired
    ReporteProgramacionService service;

    @Autowired
    ReporteHorarioView reporteHorarioView;

    @RequestMapping("reporte")
    public String reporte(Model model, HttpSession session, HttpServletRequest request) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = service.findCicloAcademico(ds.getCicloAcademico());

        model.addAttribute("ciclo", JaneHelper.from(cicloAcademico).json().toString());
        return "academico/programacion/ReporteProgramacion";
    }

    @ResponseBody
    @RequestMapping("lisReporte")
    public DynatableResponse reporte(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            List<Facultad> facultades = service.allFacultadesPre();
            facultades.forEach(facu -> {
                ObjectNode node = JsonHelper.createJson(facu, JsonNodeFactory.instance, true, new String[]{"*"});
                array.add(node);
            });

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("ReporteHorario")
    public ModelAndView ReporteHorario(@RequestParam("facultad") String facultad, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<MatriculaPreBean> listMatriculaPreBean = service.allMatriculaPregrado(ds.getCicloAcademico(), facultad);
        model.addAttribute("listMatriculaPreBean", listMatriculaPreBean);
        model.addAttribute("tipoReporte", facultad);
        return new ModelAndView(reporteHorarioView);
    }

    @ResponseBody
    @RequestMapping("allCarrera")
    public ArrayNode allCarrera(@RequestParam("nombre") String nombre) {
        log.debug("nombre:{}", nombre);
        List<Carrera> carreras = service.searchAllCarrera(nombre);
        return JaneHelper.from(carreras)
                .only("id,codigo,nombre")
                .join("modalidadEstudio", "nombre")
                .array();

    }
}
