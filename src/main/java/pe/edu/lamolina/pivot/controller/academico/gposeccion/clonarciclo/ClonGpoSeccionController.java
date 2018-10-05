package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class ClonGpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClonGpoSeccionService service;

    @ResponseBody
    @RequestMapping("clonarciclo")
    public JsonResponse clonarCiclo(CicloAcademico ciclo, Model model, HttpSession session) {
        
        JsonResponse response = new JsonResponse();
        try {
            
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
           
            CicloAcademico cicloActivo = ds.getCicloAcademico();
            
            service.clonarCiclo(cicloActivo,ciclo, ds);
            
            response.setMessage(Messages.CREATED);
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } 
        
        return response;
        
    }


}
