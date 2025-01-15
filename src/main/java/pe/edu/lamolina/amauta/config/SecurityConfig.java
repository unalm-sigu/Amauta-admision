package pe.edu.lamolina.amauta.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pe.edu.lamolina.amauta.config.helper.CustomAuthorizationRequestResolver;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/academico/**").fullyAuthenticated()
                .antMatchers("/ciclo/**").fullyAuthenticated()
                .antMatchers("/comun/**").fullyAuthenticated()
                .antMatchers("/consejeria/**").fullyAuthenticated()
                .antMatchers("/docente/**").fullyAuthenticated()
                .antMatchers("/general/**").fullyAuthenticated()
                .antMatchers("/mensajeria/**").fullyAuthenticated()
                .antMatchers("/migraciones/**").fullyAuthenticated()
                .antMatchers("/oficinas/**").fullyAuthenticated()
                .antMatchers("/rolexamen/**").fullyAuthenticated()
                .antMatchers("/seguridad/**").fullyAuthenticated()
                .antMatchers("/subvenciones/**").fullyAuthenticated()
                .antMatchers("/test/**").fullyAuthenticated()
                .antMatchers("/**").permitAll()//
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(
                        new CustomAuthorizationRequestResolver(
                                clientRegistrationRepository,
                                "/oauth2/authorization"
                        )
                )
                .and()
                .userInfoEndpoint()
                .userService(oauth2UserService);

        http.logout().logoutSuccessUrl("/");

        http.sessionManagement().invalidSessionUrl("/");

        http.exceptionHandling().accessDeniedPage("/");

        http.csrf().disable();

    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        log.info("[oauth2UserService] INICIO :::");
        DefaultOAuth2UserService delegateService = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User user = delegateService.loadUser(userRequest);
            log.info("[oauth2UserService] user={}", user);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            log.info("[oauth2UserService] registrationId={}", registrationId);

            // Personalización de usuarios según el proveedor
            if (registrationId.equals("google")) {
                log.info("[oauth2UserService] authorities={}", user.getAuthorities());
                log.info("[oauth2UserService] attributes={}", user.getAttributes());
                return new DefaultOAuth2User(
                        user.getAuthorities(),
                        user.getAttributes(),
                        "email"
                );

            } else if (registrationId.equals("microsoft")) {
                log.info("[oauth2UserService] authorities={}", user.getAuthorities());
                Map<String, Object> attributes = new HashMap<>(user.getAttributes());
                log.info("[oauth2UserService] attributes={}", attributes);
                // Personalización específica para Microsoft
                return new DefaultOAuth2User(
                        user.getAuthorities(),
                        attributes,
                        "email"
                );
            }

            log.info("[oauth2UserService] FIN :::");
            log.info("[oauth2UserService] user={}", user);
            return user;
        };
    }

}
