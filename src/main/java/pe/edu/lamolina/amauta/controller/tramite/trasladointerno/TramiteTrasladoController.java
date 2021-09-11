package pe.edu.lamolina.amauta.controller.tramite.trasladointerno;

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
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<TramiteTraslado> trTraslado = service.allTramitesByFilter(filter, ds);

            String[] mapperTramite = new String[]{
                "*",
                "universidad.*",
                "resolucion.*",
                "carrera.*",
                "carreraOrigen.*",
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
            for (TramiteTraslado tt : trTraslado) {
                ObjectNode retiroJson = JsonHelper.createJson(tt, jc, false, mapperTramiteComplex);

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
    public JsonResponse save(@RequestBody TramiteTraslado tTrasladoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.saveTramiteTraslado(tTrasladoForm, ds);
            response.setMessage("Se registró el tramite satisfactoriamente.");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        }
        return response;
    }

    @RequestMapping("{id}/reporte")
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            
            Context context = service.reporte(new Tramite(id), ds);
            model.addAllAttributes(context.getVariables());
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, model);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, model);
        }
        
        return new ModelAndView(reporteTramiteTraslado);
    }

}
