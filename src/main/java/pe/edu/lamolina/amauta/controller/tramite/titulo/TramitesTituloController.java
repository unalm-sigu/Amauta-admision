package pe.edu.lamolina.amauta.controller.tramite.titulo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
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
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Controller
@RequestMapping("academico/tramiteacademico/tramitetitulo")
public class TramitesTituloController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesTituloService tramitesTituloService;

    @Autowired
    PdfHtml reporteTramiteTitulo;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "academico/tramitescademicos/tramiteTitulo/tramitesTitulo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);
        List<TramiteTitulo> tramitesTitulos = tramitesTituloService.allTramitesByFilter(filter);

        ArrayNode array = JaneHelper.from(tramitesTitulos)
                .join("tramite")
                .join("resolucion")
                .join("usuarioAnulaTramite.persona", "apellidosNombres")
                .join("tramite.persona", "apellidosNombres")
                .join("tramite.alumno", "codigo")
                .join("tramite.alumno.planCurricular")
                .join("tramite.alumno.carrera")
                .join("tramite.alumno.carrera.facultad", "nombre")
                .join("tramite.compania")
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
    public ResponseEntity saveTitulo(@RequestBody TramiteTitulo tramiteTitulo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramitesTituloService.saveTitulo(tramiteTitulo, ds);
        return new ResponseEntity(GlobalMessages.CREATED, OK);
    }

    @ResponseBody
    @RequestMapping("anular")
    public ResponseEntity anularTitulo(@RequestBody TramiteTitulo tramiteTitulo, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        tramitesTituloService.anularTitulo(tramiteTitulo, ds);
        return new ResponseEntity(GlobalMessages.ANNULL, OK);

    }

    @RequestMapping("{idTramite}/reporte")
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        tramitesTituloService.reporte(idTramite, model, ds);
        return new ModelAndView(reporteTramiteTitulo);

    }

}
