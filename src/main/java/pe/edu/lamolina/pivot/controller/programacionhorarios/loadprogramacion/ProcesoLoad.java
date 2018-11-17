package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.util.Date;

public class ProcesoLoad {

    private String proceso;
    private Integer cantidad;
    private Integer avance;
    private Date horaInicio;
    private Date horaFinal;

    public ProcesoLoad(String proceso, Integer cantidad) {
        this.proceso = proceso;
        this.cantidad = cantidad;
        this.avance = 0;
        this.horaInicio = new Date();
    }

    public synchronized void incrementer() {
        this.avance++;
        this.horaFinal = new Date();
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(Date horaFinal) {
        this.horaFinal = horaFinal;
    }

}
