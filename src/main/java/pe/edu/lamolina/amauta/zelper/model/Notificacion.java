package pe.edu.lamolina.amauta.zelper.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Notificacion {

    private String message;
    private boolean state;
    private int totalCurso;
    private BigDecimal perCurso;
    private int currentCurso;
    private int totalSeccion;
    private BigDecimal perSeccion;
    private int currentSeccion;

    public Notificacion() {
        this.currentCurso = 0;
        this.currentSeccion = 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCurso() {
        return totalCurso;
    }

    public void setTotalCurso(int totalCurso) {
        this.totalCurso = totalCurso;
    }

    public int getCurrentCurso() {
        return currentCurso;
    }

    public void setCurrentCurso(int currentCurso) {
        this.currentCurso = currentCurso;
    }

    public int getTotalSeccion() {
        return totalSeccion;
    }

    public void setTotalSeccion(int totalSeccion) {
        this.totalSeccion = totalSeccion;
    }

    public int getCurrentSeccion() {
        return currentSeccion;
    }

    public void setCurrentSeccion(int currentSeccion) {
        this.currentSeccion = currentSeccion;
    }

    public BigDecimal getPerCurso() {
        if (this.totalCurso == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal num = new BigDecimal(this.currentCurso);
        BigDecimal den = new BigDecimal(this.totalCurso);
        BigDecimal percent = num.multiply(new BigDecimal(100)).divide(den, 2, RoundingMode.HALF_EVEN);
        return percent;
    }

    public BigDecimal getPerSeccion() {
        if (this.totalSeccion == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal num = new BigDecimal(this.currentSeccion);
        BigDecimal den = new BigDecimal(this.totalSeccion);
        BigDecimal percent = num.multiply(new BigDecimal(100)).divide(den, 2, RoundingMode.HALF_EVEN);
        return percent;
    }

    public void setPerCurso(BigDecimal perCurso) {
        this.perCurso = perCurso;
    }

    public void setPerSeccion(BigDecimal perSeccion) {
        this.perSeccion = perSeccion;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}
