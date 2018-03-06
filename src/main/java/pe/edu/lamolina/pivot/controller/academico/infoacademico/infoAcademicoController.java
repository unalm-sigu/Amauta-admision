package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/alumno")
public class infoAcademicoController {

    @Autowired
    infoAcademicoService service;

    @ResponseBody
    @RequestMapping(value = "{idAlumno}/{numeroCiclo}/avance", method = RequestMethod.GET)
    public JsonResponse alumnoListHistorial(@PathVariable("idAlumno") Long idAlumno, @PathVariable("numeroCiclo") Long numeroCiclo, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectNode objectNode = service.allAlumnosByCiclo(new Alumno(idAlumno), numeroCiclo);
            response.setData(objectNode);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
    @ResponseBody
    @RequestMapping(value = "{idAlumno}/cursoMatri", method = RequestMethod.GET)
    public JsonResponse alumnoListCursoMatri(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
             DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
             ObjectNode lst= service.allAlumnosByCursosMatri(new Alumno(idAlumno),ds.getCicloAcademico());
            response.setData(lst);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
}
