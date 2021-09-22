package pe.edu.lamolina.amauta.controller.tramite.trasladointerno;

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
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

@Controller
@RequestMapping("academico/tramiteacademico/tramiteTraslado")
public class TramiteTrasladoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteTrasladoService service;

    @Autowired
    PdfHtml reporteTramiteTraslado;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<Carrera> carreras = service.getCarreras(ds);
        ArrayNode carrerasJson = JaneHelper.from(carreras).array();
        model.addAttribute("carreras", carrerasJson);

        return "academico/tramitescademicos/tramiteTraslado/tramiteTraslado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        
        DynatableResponse json = new DynatableResponse();

        try {
            
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            
            List<TramiteTraslado> tramitesTraslado = service.allTramitesByFilter(filter, ds);

            ArrayNode array = JaneHelper.from(tramitesTraslado)
                    .join("resolucion", "descripcion")
                    .join("carrera")
                    .join("carreraOrigen")
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
    public ResponseEntity save(@RequestBody TramiteTraslado tramiteTrasladoForm, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.saveTramiteTraslado(tramiteTrasladoForm, ds);
        return new ResponseEntity(GlobalMessages.UPDATED, OK);
    }

    @RequestMapping("{id}/reporte")
    public ModelAndView reporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Context context = service.reporte(new Tramite(id), ds);
        model.addAllAttributes(context.getVariables());
        return new ModelAndView(reporteTramiteTraslado);
    }

    @ResponseBody
    @RequestMapping(value = "anular/{idTramiteTraslado}", method = RequestMethod.GET)
    public ResponseEntity anular(@PathVariable Long idTramiteTraslado, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        service.anular(idTramiteTraslado, ds.getUsuario());
        return new ResponseEntity(GlobalMessages.ANNULL, OK);

    }

}
