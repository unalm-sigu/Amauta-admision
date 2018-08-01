package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface InterceptorService {

    void saveInterceptor(ObjectNode objNode, HttpSession session);

    void saveInterceptor(ObjectNode objNode, DataSessionPivot ds);
}
