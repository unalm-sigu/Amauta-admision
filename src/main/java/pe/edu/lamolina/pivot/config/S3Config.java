package pe.edu.lamolina.pivot.config;

import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class S3Config {

    @Value("${s3-key}")
    String key;

    @Value("${s3-secret}")
    String secret;

    @Bean
    public BasicAWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(key, secret);
    }

}
