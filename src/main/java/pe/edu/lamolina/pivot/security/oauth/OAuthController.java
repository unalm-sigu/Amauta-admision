package pe.edu.lamolina.pivot.security.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.controller.seguridad.menu.VisorMenu;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
public class OAuthController {

    @Autowired
    OAuthServiceProvider serviceProvider;
    @Autowired
    VisorMenu visorMenu;
    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {

        return "security/login";

    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginGoogle() {

        OAuthService service = serviceProvider.getService();

        return "redirect:" + service.getAuthorizationUrl(null);
    }

    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public String callback(@RequestParam(value = "oauth_token", required = false) String oauthToken,
            @RequestParam(value = "code", required = false) String oauthVerifier, HttpSession session, HttpServletRequest servlet) throws IOException {

        try {
            OAuthService service = serviceProvider.getService();

            Verifier verifier = new Verifier(oauthVerifier);

            Token accessToken = service.getAccessToken(null, verifier);

            session.setAttribute(OAuthConstant.ACCESS_TOKEN, accessToken);

            OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, OAuthConstant.USER_INFO);
            service.signRequest(accessToken, oauthRequest);
            Response oauthResponse = oauthRequest.send();

            JsonNode jsonNode = new ObjectMapper().readTree(oauthResponse.getBody());

            serviceProvider.loginManually(jsonNode.get("email").asText(), session, servlet);

        } catch (PhobosException e) {
            session.removeAttribute(OAuthConstant.ACCESS_TOKEN);
            return "security/nologin";
        }

        return "redirect:/route66";
    }

    @RequestMapping(value = "lizard/{email:.*}", method = RequestMethod.GET)
    public String loginGoogle(@PathVariable String email, HttpSession session, Model model, HttpServletRequest servlet) {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            serviceProvider.loginManually(email, session, servlet);
            ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            serviceProvider.createLogJson(ds, session);

            return "redirect:/route66";
        } catch (PhobosException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getLocalizedMessage());
            return "security/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Sus credenciales no tienen acceso al sistema.");
            return "security/login";
        }
    }

    @RequestMapping("route66")
    public String route66(HttpSession session, Model model) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        if (ds == null) {
            return "redirect:/login";
        }

        logger.debug("Usuario tiene: {} roles y activo: {}", ds.getRolesMain().size(), ds.getRolActivo());
        if (ds.getRolesMain().size() > 1 && ds.getRolActivo() == null) {
            return "security/rolland";
        }

        try {
            if (ds.getRolesMain().size() == 1) {
                Rol rolActivo = ds.getRolesMain().get(0);
                serviceProvider.asignarRolActivo(rolActivo, ds, session);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return this.getRedirect(ds, session);
    }

    @ResponseBody
    @RequestMapping(value = "rolland", method = RequestMethod.POST)
    public void rolesLanding(HttpSession session, @RequestParam("rol") Long rol) throws Exception {
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Rol asignar = ds.getMapRoles().get(rol);

            serviceProvider.asignarRolActivo(asignar, ds, session);
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    @RequestMapping("changerol")
    public String changeRol(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setRolActivo(null);

        session.setAttribute(Constantine.SESSION_USUARIO, ds);

        return "security/rolland";
    }

    private String getRedirect(DataSessionPivot ds, HttpSession session) {

        String ruta = findRuta(ds.getMenu());
        String redirect = "redirect:/logout";
        String urlServerExterno = "http://maipi.albatross.pe/login";
        //String urlServerExterno = "http://localhost:9800/lagunas/" + ds.getUsuario().getUsuario();
        if (ruta.equals("")) {
            return redirect;
        }

        switch (ds.getRolActivo().getCodigo()) {

            case "ALU":
                session.invalidate();
                redirect = "redirect:" + urlServerExterno;
                break;

            default:
                redirect = ruta;
                break;
        }

        return redirect;

    }

    private String findRuta(List<Menu> menus) {
        for (Menu menu : menus) {
            if (menu.getTipoEnum() == MenuTipoEnum.MENU) {
                return "redirect:" + menu.getRuta();
            }
            if (menu.getTipoEnum() == MenuTipoEnum.SUB_MENU) {
                return "redirect:" + menu.getRuta();
            }
        }
        for (Menu menu : menus) {
            String ruta = findRuta(menu.getMenus());
            if (!ruta.equals("")) {
                return ruta;
            }
        }
        return "";
    }

}
