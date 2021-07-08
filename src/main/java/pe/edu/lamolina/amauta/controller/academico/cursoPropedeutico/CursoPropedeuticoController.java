package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;

@Controller
@RequestMapping("academico/cursoPropedeutico")
public class CursoPropedeuticoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoPropedeuticoService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, HttpServletRequest request) {

        return "academico/cursoPropedeutico/cursoPropedeutico";

    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse response = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<AlumnoCursoPropedeutico> cursoPropedeuticos = service.list(filter, ds.getCicloAcademico());

            ArrayNode arrayNode = JaneHelper.from(cursoPropedeuticos)
                    .join("seccion")
                    .join("seccion.grupoSeccion")
                    .join("seccion.grupoSeccion.curso")
                    .join("matriculaResumen")
                    .join("matriculaResumen.alumno")
                    .join("matriculaResumen.alumno.persona")
                    .join("matriculaResumen.alumno.persona.tipoDocumento")
                    .array();

            response.setData(arrayNode);
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            response.setTotal(0);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("findMatriculaResumen")
    public JsonResponse findMatriculaResumen(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<MatriculaResumen> matriculaResumens = service.findMatriculaResumen(nombre, ds.getCicloAcademico());

            ArrayNode arrayNode = JaneHelper.from(matriculaResumens)
                    .join("alumno")
                    .join("alumno.persona")
                    .array();

            response.setSuccess(Boolean.TRUE);
            response.setData(arrayNode);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("findSeccion")
    public JsonResponse findSeccion(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Seccion> secciones = service.findSeccion(nombre, ds.getCicloAcademico());

            ArrayNode arrayNode = JaneHelper.from(secciones)
                    .join("grupoSeccion")
                    .join("grupoSeccion.curso")
                    .array();

            response.setSuccess(Boolean.TRUE);
            response.setData(arrayNode);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public JsonResponse save(@RequestBody AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(alumnoCursoPropedeuticoBean, ds.getCicloAcademico(), ds.getUsuario());

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se registró satisfactoriamente el curso");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public JsonResponse update(@RequestBody AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.update(alumnoCursoPropedeuticoBean, ds.getCicloAcademico(), ds.getUsuario());

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se actualizó satisfactoriamente el curso");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "eliminardeuda/{idAlumnoCursoPropedeutico}", method = RequestMethod.GET)
    public JsonResponse eliminarDeuda(@PathVariable("idAlumnoCursoPropedeutico") Long idAlumnoCursoPropedeutico, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.eliminarDeudaAlumnoCursoPropedeutico(idAlumnoCursoPropedeutico, ds.getCicloAcademico(), ds.getUsuario());

            response.setSuccess(Boolean.TRUE);
            response.setMessage("Registro removido satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

}
