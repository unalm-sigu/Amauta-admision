package pe.edu.lamolina.amauta.controller.tramite.reincorporacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.Reincorporacion;

@Controller
@RequestMapping("academico/tramiteacademico/tramiteReincorporacion")
public class TramiteReincorporacionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteReincorporacionService reincorporacionService;

    @Autowired
    PdfHtml reporteTramiteReincorporacion;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("ciclos", JaneHelper.from(reincorporacionService.getCiclos(ds)).array());
        return "academico/tramitescademicos/tramiteReincorporacion/tramiteReincorporacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            List<Reincorporacion> trReincorporacion = reincorporacionService.allTramitesByFilter(filter, ds);

            ArrayNode array = JaneHelper.from(trReincorporacion)
                    .join("cicloReincorporacion")
                    .join("resolucion")
                    .join("facultad")
                    .join("tramite")
                    .join("tramite.persona")
                    .join("tramite.alumno")
                    .join("tramite.alumno.carrera")
                    .join("tramite.alumno.carrera.facultad")
                    .join("tramite.cicloAcademico")
                    .join("tramite.estadoTramite", "id,nombre")
                    .array();

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
    @RequestMapping("save")
    public String save(@RequestBody Reincorporacion reincorporacion, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        reincorporacionService.saveReincorporacion(reincorporacion, ds);
        return GlobalMessages.CREATED;
    }

    @RequestMapping("{idTramite}/reporte")
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        reincorporacionService.reporte(idTramite, model, ds);
        return new ModelAndView(reporteTramiteReincorporacion);
    }

}
