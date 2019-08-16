package pe.edu.lamolina.pivot.controller.reporte.dto;

public class HoraDTO {

    private TipoCeldaDTO tipoCelda;

    private String contenido;
    
    private boolean vacio;

    public enum TipoCeldaDTO {
        HEADER, BODY;
    }

    public TipoCeldaDTO getTipoCelda() {
        return tipoCelda;
    }

    public void setTipoCelda(TipoCeldaDTO tipoCelda) {
        this.tipoCelda = tipoCelda;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isVacio() {
        return vacio;
    }

}
