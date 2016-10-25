package pe.edu.lamolina.pivot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.lamolina.pivot.security.oauth.OAuthServiceConfig;

@Configuration
@ConfigurationProperties(prefix = "oauth.google")
public class OauthConfig {

    @Value("${api}")
    String api;

    @Value("${key}")
    String key;

    @Value("${secret}")
    String secret;

    @Value("${callback}")
    String callback;

    @Bean
    public OAuthServiceConfig getOauthServiceConfig() throws ClassNotFoundException {
        OAuthServiceConfig osc = new OAuthServiceConfig();
        osc.setCallback(this.callback);
        osc.setKey(this.key);
        osc.setSecret(this.secret);
        osc.setApiClass(Class.forName(this.api));

        return osc;
    }

}
