package pe.edu.lamolina.pivot.controller.interceptor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.interceptor.UserLogger;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.dao.interceptor.UserLoggerDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class InterceptorServiceImpl implements InterceptorService {

    @Autowired
    UserLoggerDAO userLogger;

    @Autowired
    DespliegueConfig despliegueConfig;

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInterceptor(ObjectNode objNode, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        System.err.println("SERVLET --->");
        UserLogger obj = new UserLogger();
        String[] ls = new String[ds.getRoles().size()];
        int i = 0;
        for (Rol role : ds.getRoles()) {
            ls[i] = role.getCodigo();
            i++;
        }
        Sistema objSis = new Sistema();
        objSis.setId(despliegueConfig.getSistema());
        obj.setAccion(objNode.get("tipo").toString());
        obj.setBrowser(ds.getBrowser());
        obj.setDireccionIp(ds.getDireccionIp());
        obj.setSistemaOperativo(ds.getSistemaOperativo());
        obj.setFechaRegistro(new Date());
        obj.setData(objNode.get("data").toString());
        obj.setCiclo(ds.getCicloAcademico().getDescripcion());
        obj.setRoles(Arrays.toString(ls).toString());
        obj.setUsuario(ds.getPersona().getNombreCompleto());
        obj.setSistema(objSis);
        userLogger.save(obj);

    }

}
