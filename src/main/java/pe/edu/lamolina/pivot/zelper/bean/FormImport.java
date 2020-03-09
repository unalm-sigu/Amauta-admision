package pe.edu.lamolina.pivot.zelper.bean;

public class FormImport {

    Long idUsuario;
    Long idAlumno;
    Long idOrientacion;
    String token;

    public Long getIdAlumno() {
        return idAlumno;
    }

    public Long getIdOrientacion() {
        return idOrientacion;
    }

    public void setIdOrientacion(Long idOrientacion) {
        this.idOrientacion = idOrientacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
