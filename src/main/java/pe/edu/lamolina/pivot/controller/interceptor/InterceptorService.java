package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpServletRequest;

public interface InterceptorService {

    void saveInterceptor (HttpServletRequest servlet, ObjectNode objNode);
}
