package pe.edu.lamolina.amauta.controller.tramite.cambioplancurricular;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.pdfHtml.PdfHtmlSimplified;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;

@Controller
@RequestMapping("academico/tramiteacademico/cambioplancurricular")
public class CambioPlanCurricularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CambioPlanCurricularService service;

    @Autowired
    PdfHtmlSimplified reporteCambioPlanCurricularPdf;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("ciclos", JaneHelper.from(service.getCiclosVeinte()).only("id,descripcion,codigo")
                .array().toString());

        return "academico/tramitescademicos/cambioplancurricular/cambioplancurricular";

    }

    @ResponseBody
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode array = JaneHelper.from(service.allTramitesByFilter(filter, ds))
                    .join("cicloReadmitido")
                    .join("resolucion")
                    .join("facultad")
                    .join("tramite")
                    .join("tramite.persona")
                    .join("tramite.alumno")
                    .join("tramite.alumno.carrera")
                    .join("tramite.alumno.carrera.facultad")
                    .join("tramite.cicloAcademico")
                    .join("estadoTramite")
                    .array();

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public JsonResponse save(@RequestBody CambioPlanCurricular cambioPlanCurricular, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(cambioPlanCurricular, ds);
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{idCambioPlanCurricular}/anular", method = RequestMethod.GET)
    public JsonResponse anular(@PathVariable Long idCambioPlanCurricular, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.anular(idCambioPlanCurricular, ds);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "{id}/reporte", method = RequestMethod.GET)
    public ModelAndView bachillerReporte(Model model, HttpSession session, HttpServletResponse response, @PathVariable Long id) {

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.reporte(model, id, ds);
            return new ModelAndView(reporteCambioPlanCurricularPdf);

        } catch (PhobosException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView(reporteCambioPlanCurricularPdf);

    }

    @ResponseBody
    @RequestMapping("searchAlumno")
    public JsonResponse searchAlumno(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Alumno> alumnos = service.searchAlumno(nombre, ds);
            ArrayNode jsonList = JaneHelper.from(alumnos).only("id,codigo")
                    .join("modalidadEstudio", "id,nombre")
                    .join("carrera.facultad", "codigo,nombre")
                    .join("carrera", "codigo,nombre")
                    .join("persona", "numeroDocIdentidad,apellidosNombres,nombreCompleto,rutaFoto")
                    .join("persona.tipoDocumento")
                    .array();

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "searchPlanCurricular/{idAlumno}", method = RequestMethod.GET)
    public JsonResponse searchPlanCurricular(@PathVariable Long idAlumno, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            response.setData(service.searchPlanCurricular(idAlumno, ds));
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
