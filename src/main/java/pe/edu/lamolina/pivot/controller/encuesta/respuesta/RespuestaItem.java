package pe.edu.lamolina.pivot.controller.encuesta.respuesta;

public class RespuestaItem {

    private String contenido;

    private Integer cantidad;

    public RespuestaItem(String contenido, Integer cantidad) {
        this.contenido = contenido;
        this.cantidad = cantidad;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
