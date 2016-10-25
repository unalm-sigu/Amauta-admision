package pe.edu.lamolina.pivot.security.oauth;

import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import javax.servlet.http.HttpSession;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Service
public class OAuthServiceProviderImp implements OAuthServiceProvider {

    @Autowired
    OAuthServiceConfig config;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(config.getApiClass())
                .apiKey(config.getKey())
                .apiSecret(config.getSecret())
                .callback(config.getCallback())
                .scope("https://www.googleapis.com/auth/userinfo.email "
                        + "https://www.googleapis.com/auth/userinfo.profile")
                .build();
    }

    @Override
    public void loginManually(String email, HttpSession session) {

        SecurityContext cntx = SecurityContextHolder.getContext();

        Collection<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("USUARIO"));

        if (authorities.isEmpty()) {
            throw new PhobosException("Usuario sin rol asignado");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, email, authorities);
        cntx.setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, cntx);

        DataSession dataSession = new DataSession();
        dataSession.setEmail(email);
        session.setAttribute(Constantine.SESSION_USUARIO, dataSession);
    }

}
