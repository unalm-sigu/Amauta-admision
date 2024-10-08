package pe.edu.lamolina.amauta.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@ControllerAdvice
public class ExceptionConfig {

    @ResponseBody
    @ExceptionHandler
    public JsonResponse handleError(HttpServletRequest req, HttpServletResponse res, Exception ex) throws IOException, ServletException {

        log.error("Excepcion \"{}\" generada en \"{}\"", ex.getLocalizedMessage(), req.getRequestURL());
        JsonResponse response = new JsonResponse();
        response.setTotal(0);

        if (ex instanceof PhobosException) {
            PhobosException exception = (PhobosException) ex;
            pe.albatross.zelpers.miscelanea.ExceptionHandler.handlePhobosEx(exception, response);
            return response;

        } else if (ex instanceof RuntimeException) {
            RuntimeException exception = (RuntimeException) ex;
            pe.albatross.zelpers.miscelanea.ExceptionHandler.handleSpecial(exception, response, GlobalMessages.FK_ERROR_DELETE);
            return response;

        } else {
            pe.albatross.zelpers.miscelanea.ExceptionHandler.handleException(ex, response);
            log.error("Exception general", ex);
        }

        return response;
    }

}
