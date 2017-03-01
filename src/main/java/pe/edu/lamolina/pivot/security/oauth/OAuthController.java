package pe.edu.lamolina.pivot.security.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
public class OAuthController {

    @Autowired
    OAuthServiceProvider serviceProvider;

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
            @RequestParam(value = "code", required = false) String oauthVerifier, HttpSession session) throws IOException {

        try {
            OAuthService service = serviceProvider.getService();

            Verifier verifier = new Verifier(oauthVerifier);

            Token accessToken = service.getAccessToken(null, verifier);

            session.setAttribute(OAuthConstant.ACCESS_TOKEN, accessToken);

            OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, OAuthConstant.USER_INFO);
            service.signRequest(accessToken, oauthRequest);
            Response oauthResponse = oauthRequest.send();

            JsonNode jsonNode = new ObjectMapper().readTree(oauthResponse.getBody());

            serviceProvider.loginManually(jsonNode.get("email").asText(), session);

        } catch (PhobosException e) {
            session.removeAttribute(OAuthConstant.ACCESS_TOKEN);
            return "security/nologin";
        }

        return "redirect:/route66";
    }

    @RequestMapping(value = "lagunas/{email:.*}", method = RequestMethod.GET)
    public String loginGoogle(@PathVariable String email, HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        serviceProvider.loginManually(email, session);
        return "redirect:/route66";
    }

    @RequestMapping("route66")
    public String route66(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        if (ds == null) {
            return "redirect:/login";
        }

        logger.debug("Usuario tiene: {} roles y activo: {}", ds.getRoles().size(), ds.getRolActivo());

        if (ds.getRoles().size() > 1 && ds.getRolActivo() == null) {
            return "security/rolland";

        } else if (ds.getRoles().size() == 1) {
            Rol rolActivo = ds.getRoles().get(0);
            ds.setRolActivo(rolActivo);

            session.setAttribute(Constantine.SESSION_USUARIO, ds);
        }

        String redirect = this.getRedirect(ds);

        return redirect;
    }

    @ResponseBody
    @RequestMapping(value = "rolland", method = RequestMethod.POST)
    public void rolesLanding(HttpSession session, @RequestParam("rol") Long rol) throws Exception {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Rol asignar = ds.getMapRoles().get(rol);

        ds.setRolActivo(asignar);

        session.setAttribute(Constantine.SESSION_USUARIO, ds);
    }

    @RequestMapping("changerol")
    public String chnageRol(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setRolActivo(null);

        session.setAttribute(Constantine.SESSION_USUARIO, ds);

        return "security/rolland";
    }

    private String getRedirect(DataSessionPivot ds) {

        String redirect = "redirect:/logout";

        switch (ds.getRolActivo().getCodigo()) {
            case "DPTO":
                redirect = "redirect:/academico/systemcalifica/sistema";
                break;

            case "DOC":
                Docente docente = ds.getDocente();
                if (docente != null) {
                    redirect = "redirect:/academico/docente/cargaacademica";
                }
                break;

            case "IOREA":
                redirect = "redirect:/general/personaperfil";
                break;

            case "OREA":
                redirect = "redirect:/academico/acta";
                break;

            default:
                logger.debug("No se identifica acceso para el rol: {} ", ds.getRolActivo().getCodigo());
                break;

        }

        return redirect;

    }

}
