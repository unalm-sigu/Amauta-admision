package pe.edu.lamolina.amauta.controller.docente.bloqueoIngresante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("docente/matricula/bloqueo")
public class MatriculaBloqueoIngresanteController {

    @Autowired
    MatriculaBloqueoIngresanteService service;
    @Autowired
    ReporteBloqueoIngresanteExcelView reporteBloqueoIngresanteExcelView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        if (ds.getUsuario() == null) {
            return "redirect:/";
        }
        return "docente/bloqueo/bloqueoIngresantes";
    }

    @ResponseBody
    @RequestMapping(value = "all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        DynatableResponse dynatable = new DynatableResponse();
        dynatable.setTotal(0);
        List<MatriculaBloqueoIngresante> matriculaBloqueoIngresantes = service.allByDynatable(filter, ds);
        ArrayNode array = JaneHelper.from(matriculaBloqueoIngresantes)
                .only("id,rm,rv,matematica,fisica,quimica,biologia,inscrito,matricula,fechaRegistro")
                .join("ingresante", "codigo")
                .join("ingresante.postulante.persona", "paterno,materno,nombres,apellidosNombres,celular,telefono,email,emailCompania")
                .join("ingresante.postulante.modalidadIngreso", "nombre")
                .join("ingresante.carrera", "nombre")
                .array();
        dynatable.setData(array);
        dynatable.setTotal(filter.getTotal());
        dynatable.setFiltered(filter.getFiltered());
        return dynatable;
    }

    @ResponseBody
    @RequestMapping(value = "copia", method = RequestMethod.POST)
    public JsonResponse copiaIngresantesAdmision(HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            String mensaje = service.copiaIngresantesAdmision(ds);
            response.setSuccess(Boolean.TRUE);
            response.setMessage(mensaje);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "actualizar/{id}", method = RequestMethod.PUT)
    public JsonResponse actualizarMatricula(@PathVariable("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.actualizarMatricula(id, ds);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping(value = "reporteAlumnos/{ciclo}", method = RequestMethod.POST)
    public ModelAndView reporteGeneral(@PathVariable("ciclo") Long ciclo, Model model, HttpSession session) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<MatriculaBloqueoIngresante> matriculaBloqueoIngresantes = service.allByCicloAcademico(ciclo);

        model.addAttribute("matriculaBloqueoIngresantes", matriculaBloqueoIngresantes);

        return new ModelAndView(reporteBloqueoIngresanteExcelView);
    }

}
