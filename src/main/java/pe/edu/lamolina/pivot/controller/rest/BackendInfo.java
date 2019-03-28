package pe.edu.lamolina.pivot.controller.rest;

import org.springframework.stereotype.Component;

@Component
public class BackendInfo {

    private String url;

    private String token;

    public BackendInfo(String url, String token) {
        this.url = url;
        this.token = token;
    }

    public BackendInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
