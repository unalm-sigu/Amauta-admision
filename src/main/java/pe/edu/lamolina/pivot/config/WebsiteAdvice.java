package pe.edu.lamolina.pivot.config;

import javax.servlet.http.HttpServletRequest;
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
    public String handleError(HttpServletRequest req, Exception ex) throws Exception {
        logger.error("\nGENERAL ERROR: {} {}", req.getRequestURL(), ex.getLocalizedMessage(), ex);
        return "redirect:/";
    }

}
