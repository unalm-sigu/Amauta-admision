package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class FusionSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FusionSeccionService service;

    @ResponseBody
    @RequestMapping("allalumno")
    public JsonResponse allAlumno(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Alumno> alumnos = service.allAlumnoBySeccion(seccion);

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);
            for (Alumno alumno : alumnos) {
                ObjectNode node = JsonHelper.createJson(alumno, jFactory, true,
                        new String[]{
                            "*",
                            "persona.*",
                            "carrera.*",
                            "situacionAcademica.*"
                        });
                array.add(node);
            }

            response.setData(array);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("trasladar")
    public JsonResponse trasladar(@RequestBody Fusion fusion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloActivo = ds.getCicloAcademico();

            service.trasladar(fusion, cicloActivo, ds);

            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allsecciondisponible")
    public JsonResponse allSeccionDisponible(Seccion seccion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<Seccion> secciones = service.allSeccionDisponible(seccion);

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jFactory);

            for (Seccion seccionDb : secciones) {
                if (seccionDb.getDisponiblesCachimbos() < 1) {
                    continue;
                }
                ObjectNode node = JsonHelper.createJson(seccionDb, jFactory, true,
                        new String[]{
                            "id",
                            "disponiblesCachimbos",
                            "codigo",
                            "codigo2",
                            "grupoSeccion.id",
                            "grupoSeccion.codigo",
                            "aula.id",
                            "aula.codigo",
                            "aula.nombre",
                            "aula.aforo"
                        });
                array.add(node);
            }

            response.setData(array);
            response.setMessage(Messages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
