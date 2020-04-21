package pe.edu.lamolina.amauta.security.oauth;

public class OAuthServiceConfig {

    private String key;

    private String secret;

    private String callback;

    private Class apiClass;

    public OAuthServiceConfig(String key, String secret, String callback, Class apiClass) {
        this.key = key;
        this.secret = secret;
        this.callback = callback;
        this.apiClass = apiClass;
    }

    public OAuthServiceConfig() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Class getApiClass() {
        return apiClass;
    }

    public void setApiClass(Class apiClass) {
        this.apiClass = apiClass;
    }
}
