package pe.edu.lamolina.amauta.security.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pe.edu.lamolina.amauta.controller.seguridad.menu.VisorMenu;

@Service
public class InterceptorAmauta extends HandlerInterceptorAdapter {

    @Autowired
    VisorMenu visorMenu;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("RRRRRR :::: " + request.getRequestURL());
//        System.out.println("QQQQQQ :::: " + request.getPathInfo());
//        System.out.println("QQQQQQ :::: " + request.getPathTranslated());
//        System.out.println("QQQQQQ :::: " + request.getRequestedSessionId());
//        System.out.println("QQQQQQ :::: " + request.getServletPath());
//        System.out.println("QQQQQQ :::: " + request.getServerName());
//        System.out.println("QQQQQQ :::: " + request.getCharacterEncoding());
//        System.out.println("existen " + visorMenu);
//        System.out.println("existen " + visorMenu.getMapMenus());
//        System.out.println("existen " + visorMenu.getMapMenus().size());
        return super.preHandle(request, response, handler); //To change body of generated methods, choose Tools | Templates.
    }

}
