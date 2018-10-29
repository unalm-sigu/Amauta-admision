package pe.edu.lamolina.pivot.controller.academico.gposeccion.precioseccion;

import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class PrecioSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PrecioSeccionService service;

    @ResponseBody
    @RequestMapping("saveprecioseccion")
    public JsonResponse savePrecioSeccion(@RequestBody Seccion precioSeccion, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);           
            service.savePrecioSeccion(precioSeccion, ds);
            response.setMessage(" Guardado satisfactoriamnente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }
   
}
