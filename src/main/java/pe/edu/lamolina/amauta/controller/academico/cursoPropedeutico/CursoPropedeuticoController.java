package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        return "academico/cursoPropedeutico/cursoPropedeutico";
    }

    @ResponseBody
    @RequestMapping("findMatriculaResumen")
    public JsonResponse findMatriculaResumen(@RequestParam("nombre") String nombre, HttpSession session) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<MatriculaResumen> matriculaResumens = service.findMatriculaResumen(nombre, ds.getCicloAcademico());
            for (MatriculaResumen matriculaResumen : matriculaResumens) {

                arrayNode.add(JsonHelper.createJson(matriculaResumen, JsonNodeFactory.instance, new String[]{
                    "*",
                    "alumno.*",
                    "alumno.persona.*"
                }));

            }
            response.setSuccess(Boolean.TRUE);
            response.setData(arrayNode);

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("findSeccion")
    public JsonResponse findSeccion(@RequestParam("nombre") String nombre, HttpSession session) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<Seccion> secciones = service.findSeccion(nombre, ds.getCicloAcademico());
            for (Seccion seccion : secciones) {

                arrayNode.add(JsonHelper.createJson(seccion, JsonNodeFactory.instance, new String[]{
                    "*",
                    "grupoSeccion.*",
                    "grupoSeccion.curso.*"
                }));

            }
            response.setSuccess(Boolean.TRUE);
            response.setData(arrayNode);

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public JsonResponse save(@RequestBody AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, HttpSession session) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.save(alumnoCursoPropedeuticoBean, ds.getCicloAcademico(), ds.getUsuario());

            response.setData(arrayNode);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se registró satisfactoriamente el curso");

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public JsonResponse update(@RequestBody AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, HttpSession session) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        JsonResponse response = new JsonResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.update(alumnoCursoPropedeuticoBean, ds.getCicloAcademico(), ds.getUsuario());

            response.setData(arrayNode);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se actualizó satisfactoriamente el curso");

        } catch (PhobosException e) {
            e.printStackTrace();
            ExceptionHandler.handlePhobosEx(e, response);
            e.printStackTrace();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        DynatableResponse response = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<AlumnoCursoPropedeutico> cursoPropedeuticos = service.list(filter, ds.getCicloAcademico());
            for (AlumnoCursoPropedeutico cursoPropedeutico : cursoPropedeuticos) {

                arrayNode.add(JsonHelper.createJson(cursoPropedeutico, JsonNodeFactory.instance, new String[]{
                    "*",
                    "seccion.*",
                    "seccion.grupoSeccion.*",
                    "seccion.grupoSeccion.curso.*",
                    "matriculaResumen.*",
                    "matriculaResumen.alumno.*",
                    "matriculaResumen.alumno.persona.*",
                    "matriculaResumen.alumno.persona.tipoDocumento.*"
                }));
            }
            response.setData(arrayNode);
            response.setTotal(filter.getTotal());
            response.setFiltered(filter.getFiltered());

        } catch (PhobosException e) {
            e.printStackTrace();
//            ExceptionHandler.handlePhobosEx(e, response.);
            e.printStackTrace();
        } catch (Exception e) {
//            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
