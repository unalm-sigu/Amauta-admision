package pe.edu.lamolina.pivot.security.oauth;

import com.github.scribejava.core.oauth.OAuth20Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OAuthServiceProvider {

    OAuth20Service getService();

    void loginManually(String email, HttpSession session, HttpServletRequest servlet);

    void createLogJson(DataSessionPivot ds, HttpSession session);

    void createLogJsonLogout(DataSessionPivot ds, HttpSession session);

    //List<Menu> allMenuRolActivo(Rol rolAsignar, Sistema sistema);

    void asignarRolActivo(Rol asignar, DataSessionPivot ds, HttpSession session);

}
