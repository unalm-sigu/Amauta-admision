package pe.edu.lamolina.amauta.controller.tramite.retirocicloexcepcional;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.RetiroCiclo;

@Slf4j
@Controller
@RequestMapping("academico/tramiteacademico/tramiteRetiroExcepcional")
public class TramiteRetiroExcepcionalController {

    @Autowired
    TramiteRetiroExcepcionalService service;

    @Autowired
    PdfHtml reporteTramiteRetiroExcepcionalPdf;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = service.getCiclosVeinte(ds);
        ArrayNode arrayCiclos = JaneHelper.from(cicloAcademicos).array();
        model.addAttribute("ciclos", arrayCiclos.toString());
        return "academico/tramitescademicos/tramiteRetiroExcepcional/tramiteRetiroExcepcional";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<RetiroCiclo> trCiclos = service.allTramitesByFilter(filter, ds);

        ArrayNode array = JaneHelper.from(trCiclos)
                .join("tramite")
                .join("tramite.persona")
                .join("tramite.alumno", "id,codigo")
                .join("tramite.alumno.carrera", "id,nombre")
                .join("tramite.alumno.carrera.facultad", "id,nombre")
                .join("cicloAcademico", "id,descripcion,codigo,descripcion2")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());

        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public String save(@RequestBody RetiroCiclo retiro, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveRetiro(retiro, ds);
        return GlobalMessages.CREATED;
    }

    @RequestMapping("{idTramite}/reporte")
    public ModelAndView tramiteRetiroExcepcionalReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.reporte(idTramite, ds, model);
        return new ModelAndView(reporteTramiteRetiroExcepcionalPdf);

    }

    @ResponseBody
    @RequestMapping("anular")
    public String anular(@RequestBody RetiroCiclo retiroCiclo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.anular(retiroCiclo, ds);
        return GlobalMessages.ANNULL;
    }
}
