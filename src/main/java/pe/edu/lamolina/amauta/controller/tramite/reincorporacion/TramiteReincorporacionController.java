package pe.edu.lamolina.amauta.controller.tramite.reincorporacion;

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
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.pdfHtml.PdfHtmlSimplified;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.tramite.Reincorporacion;

@Controller
@RequestMapping("academico/tramiteacademico/tramiteReincorporacion")
public class TramiteReincorporacionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteReincorporacionService reincorporacionService;

    @Autowired
    PdfHtmlSimplified reporteTramiteReincorporacion;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<CicloAcademico> cicloAcademicos = reincorporacionService.getCiclos(ds);

        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        for (CicloAcademico cicloAcademico : cicloAcademicos) {
            arr.add(JsonHelper.createJson(cicloAcademico, JsonNodeFactory.instance, new String[]{
                "*"
            }));
        }
        model.addAttribute("ciclos", arr);
        return "academico/tramitescademicos/tramiteReincorporacion/tramiteReincorporacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse listTramites(DynatableFilter filter,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<Reincorporacion> trReincorporacion = reincorporacionService.allTramitesByFilter(filter, ds);

            String[] mapperTramite = new String[]{
                "cicloReincorporacion.*",
                "resolucion.*",
                "facultad.*",
                "tramite.*",
                "tramite.persona.*",
                "tramite.alumno.*",
                "tramite.alumno.carrera.*",
                "tramite.alumno.carrera.facultad.*",
                "tramite.cicloAcademico.*"
            };

            String[] mapperEstadoTramite = new String[]{
                "tramite.estadoTramite.nombre",
                "tramite.estadoTramite.id",
                "tramite.estadoTramite.nombre"
            };

            String[] mapperTramiteComplex = (String[]) ArrayUtils.addAll(mapperTramite, mapperEstadoTramite);

            JsonNodeFactory jc = JsonNodeFactory.instance;
            for (Reincorporacion rc : trReincorporacion) {
                ObjectNode retiroJson = JsonHelper.createJson(rc, jc, false, mapperTramiteComplex);

                array.add(retiroJson);
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

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody Reincorporacion reincorporacion, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            reincorporacionService.saveReincorporacion(reincorporacion, ds);
            response.setMessage("Se registró el tramite satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @RequestMapping("{idTramite}/reporte")
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long idTramite) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            Context context = reincorporacionService.reporte(idTramite, ds);
            model.addAllAttributes(context.getVariables());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }

        return new ModelAndView(reporteTramiteReincorporacion);
    }

}
