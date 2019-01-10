package pe.edu.lamolina.pivot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "despliegue")
public class DespliegueConfig {

    @Value("${prospecto}")
    String prospecto;

    @Value("${mailer}")
    Boolean mailer;

    @Value("${lagunas}")
    Boolean lagunas;

    @Value("${emails}")
    String emails;

    @Value("${copias}")
    String copias;

    @Value("${s3}")
    Boolean s3;

    @Value("${tawkto}")
    Boolean tawkto;

    @Value("${sistema}")
    Long sistema;

    @Value("${ambiente}")
    String ambiente;

    @Value("${usuarioSistema}")
    String usuarioSistema;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Long getSistema() {
        return sistema;
    }

    public void setSistema(Long sistema) {
        this.sistema = sistema;
    }

    public String getProspecto() {
        return prospecto;
    }

    public void setProspecto(String prospecto) {
        this.prospecto = prospecto;
    }

    public Boolean getMailer() {
        return mailer;
    }

    public void setMailer(Boolean mailer) {
        this.mailer = mailer;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getCopias() {
        return copias;
    }

    public void setCopias(String copias) {
        this.copias = copias;
    }

    public Boolean getLagunas() {
        return lagunas;
    }

    public void setLagunas(Boolean lagunas) {
        this.lagunas = lagunas;
    }

    public Boolean getS3() {
        return s3;
    }

    public void setS3(Boolean s3) {
        this.s3 = s3;
    }

    public Boolean getTawkto() {
        return tawkto;
    }

    public void setTawkto(Boolean tawkto) {
        this.tawkto = tawkto;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

}
