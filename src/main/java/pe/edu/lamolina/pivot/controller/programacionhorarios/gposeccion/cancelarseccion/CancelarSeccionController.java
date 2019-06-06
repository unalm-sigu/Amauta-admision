package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cancelarseccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class CancelarSeccionController {
    
    @Autowired
    CancelarSeccionService service;
    
    public List<MatriculaSeccion> matriculasSecciones(Seccion seccion) {
        return service.allMatriculaSeccionBySeccion(seccion);
    }
    
    @ResponseBody
    @RequestMapping("loadCancelarSeccionComp")
    public JsonResponse loadCancelarSeccionComp(@RequestParam("seccion") Long seccionId, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<MatriculaSeccion> matriculasSeccion = service.allMatriculaSeccionBySeccion(new Seccion(seccionId));
            ArrayNode jMatriculasSeccion = new ArrayNode(jc);
            for (MatriculaSeccion matriculaSeccion : matriculasSeccion) {
                ObjectNode jMatriculaSeccion = JsonHelper.createJson(matriculaSeccion, jc, true,
                        new String[]{
                            "matriculaResumen.alumno.codigo",
                            "matriculaResumen.alumno.persona.apellidosNombres"
                        });
                jMatriculasSeccion.add(jMatriculaSeccion);
            }
            response.setData(jMatriculasSeccion);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }
    
}
