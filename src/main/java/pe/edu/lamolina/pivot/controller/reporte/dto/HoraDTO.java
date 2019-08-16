package pe.edu.lamolina.pivot.controller.reporte.dto;

import com.itextpdf.text.Element;

public class HoraDTO {

    private int alineacion;
    
    private TipoCeldaDTO tipoCelda;

    private String contenido;
    
    private boolean vacio;

    public int getAlineacion() {
        return alineacion;
    }

    public void setAlineacion(int alineacion) {
        this.alineacion = alineacion;
    }

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

    public void setVacio(boolean vacio) {
        this.vacio = vacio;
    }

 
    
    
    
    

}
