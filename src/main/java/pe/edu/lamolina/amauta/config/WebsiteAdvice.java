package pe.edu.lamolina.amauta.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebsiteAdvice {

    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public String handleError(HttpServletRequest req, HttpServletResponse res, Exception ex) throws Exception {
        logger.error("\nGENERAL ERROR: {} {}", req.getRequestURL(), ex.getLocalizedMessage(), ex);
        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        return "redirect:/";
    }

}
