package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.servlet.http.HttpSession;

public interface InterceptorService {

    void saveInterceptor (ObjectNode objNode,HttpSession session);
}
