package pe.edu.lamolina.amauta.controller.tramite.bachiller;

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
import pe.edu.lamolina.amauta.zelper.pdf.PdfPieDePaginaHtml;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Controller
@RequestMapping("academico/tramiteacademico/tramitebachiller")
public class TramitesBachillerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesBachillerService tramitesBachillerService;

    @Autowired
    PdfPieDePaginaHtml reporteTramiteBachiller;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "academico/tramitescademicos/tramitebachiller/tramitesBachiller";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);
        List<TramiteBachiller> tramitesBachiller = tramitesBachillerService.allTramitesByFilter(filter);

        ArrayNode array = JaneHelper.from(tramitesBachiller)
                .join("resolucion")
                .join("usuarioAnulaTramite.persona", "apellidosNombres")
                .join("tramite")
                .join("tramite.alumno")
                .join("tramite.alumno.persona")
                .join("tramite.alumno.planCurricular")
                .join("tramite.alumno.carrera")
                .join("tramite.alumno.carrera.facultad")
                .join("tramite.cicloAcademico")
                .join("tramite.tipoTramite")
                .join("tramite.tipoTramite.oficina")
                .join("tramite.userRegistro")
                .join("tramite.userRegistro.persona")
                .join("tramite.userRespuesta")
                .join("tramite.formularioEstadoTramite")
                .join("tramite.estadoTramite", "id,nombre")
                .array();

        json.setData(array);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public String bachiller(@RequestBody TramiteBachiller tramiteBachiller, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramitesBachillerService.saveBachiller(tramiteBachiller, ds);
        return GlobalMessages.CREATED;
    }

    @ResponseBody
    @RequestMapping("anular")
    public String anular(@RequestBody TramiteBachiller tramiteBachiller, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramitesBachillerService.anular(tramiteBachiller, ds);
        return GlobalMessages.ANNULL;
    }

    @RequestMapping(value = "{idTramite}/reporte", method = RequestMethod.GET)
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramitesBachillerService.reporte(idTramite, model, ds);
        return new ModelAndView(reporteTramiteBachiller);

    }

}
