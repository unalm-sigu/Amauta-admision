package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface InterceptorService {

    void saveInterceptor (HttpServletRequest servlet, ObjectNode objNode,HttpSession session);
}
