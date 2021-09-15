package pe.edu.lamolina.amauta.controller.tramite.bachiller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
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
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Controller
@RequestMapping("academico/tramiteacademico/tramitebachiller")
public class TramitesBachillerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramitesBachillerService tramitesBachillerService;

    @Autowired
    PdfHtml reporteTramiteBachiller;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        return "academico/tramitescademicos/tramitebachiller/tramitesBachiller";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        
        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<TramiteBachiller> tramitesBachiller = tramitesBachillerService.allTramitesByFilter(filter, ds);

            ArrayNode array = JaneHelper.from(tramitesBachiller)
                    .join("resolucion")
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

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse bachiller(@RequestBody TramiteBachiller tramiteBachiller, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            tramitesBachillerService.saveBachiller(tramiteBachiller, ds);
            response.setMessage("Tramite registrado correctamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anular(@RequestBody TramiteBachiller tramiteBachiller, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            tramitesBachillerService.anular(tramiteBachiller, ds);
            response.setMessage("Tramite anulado correctamente.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("{idTramite}/reporte")
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            Context context = tramitesBachillerService.reporte(idTramite, ds);

            model.addAllAttributes(context.getVariables());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }

        return new ModelAndView(reporteTramiteBachiller);
    }

}
