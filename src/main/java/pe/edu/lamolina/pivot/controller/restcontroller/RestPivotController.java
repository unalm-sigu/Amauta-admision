package pe.edu.lamolina.pivot.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.zelper.bean.FormImport;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@RestController
@RequestMapping("amauta/rest")
public class RestPivotController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestPivotService service;

    @Autowired
    InfoAcademicoService infoAcademicoService;

    @Autowired
    PromedioService promedioService;

    @ResponseBody
    @RequestMapping(value = "cambioOrientacion", method = RequestMethod.POST)
    public JsonResponse cambioOrientacion(@RequestBody String node,
            HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {
            ObjectMapper mapper = new ObjectMapper();
            FormImport json = (FormImport) mapper.readValue(node, FormImport.class);
            service.validateToken(json);

            Alumno alumno = new Alumno(json.getIdAlumno());
            OrientacionCarrera orientacion = json.getIdOrientacion() != null ? new OrientacionCarrera(json.getIdOrientacion()) : null;
            DataSessionPivot ds = new DataSessionPivot();
            ds.setUsuario(new Usuario(json.getIdUsuario()));
            infoAcademicoService.cambiarOrientacion(alumno, orientacion, ds);

            response.setSuccess(true);

        } catch (PhobosException e) {
            response.setSuccess(false);
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "calcularPromedioAlumno", method = RequestMethod.POST)
    public JsonResponse calcularPromedioAlumno(@RequestBody String node, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ObjectMapper mapper = new ObjectMapper();
            FormImport json = (FormImport) mapper.readValue(node, FormImport.class);
            service.validateToken(json);

            Alumno alumno = new Alumno(json.getIdAlumno());
            DataSessionPivot ds = new DataSessionPivot();
            ds.setUsuario(new Usuario(json.getIdUsuario()));
            promedioService.calcularSituacionAcademica(alumno, ds);

            response.setSuccess(true);
        } catch (PhobosException e) {
            response.setSuccess(false);
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
