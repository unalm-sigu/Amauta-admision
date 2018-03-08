package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.interceptor.UserLogger;
import pe.edu.lamolina.pivot.dao.interceptor.UserLoggerDAO;

@Service
@Transactional(readOnly = true)
public class InterceptorServiceImpl implements InterceptorService {
    
    @Autowired
    UserLoggerDAO userLogger;
    
    @Override
    @Transactional
    public void saveInterceptor(HttpServletRequest servlet, ObjectNode objNode) {
        UserLogger obj = new UserLogger();

        obj.setAccion(objNode.get("tipo").toString());
        obj.setBrowser(servlet.getHeader("User-Agent"));
        obj.setDireccionIp(servlet.getRemoteAddr());
        obj.setSistemaOperativo(getClientOS(servlet));
        obj.setFechaRegistro(new Date());
        obj.setData(objNode.toString());
        obj.setCiclo(objNode.get("ciclo").toString());
        obj.setRoles("ROL");
        obj.setUsuario("David");
        userLogger.save(obj);
        
    }

    public String getClientOS(HttpServletRequest request) {
        final String browserDetails = request.getHeader("User-Agent");

        //=================OS=======================
        final String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            return "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            return "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            return "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            return "Android";
        } else if (lowerCaseBrowser.contains("iphone")) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + browserDetails;
        }
    }
}
