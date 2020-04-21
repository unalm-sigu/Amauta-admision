package pe.edu.lamolina.amauta.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.interceptor.UserLogger;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface InterceptorService {

    void saveInterceptor(ObjectNode objNode, HttpSession session);

    void saveInterceptor(ObjectNode objNode, DataSessionPivot ds);

    void saveInterceptor(ObjectNode objNode, Exception exception, DataSessionPivot ds);

}
