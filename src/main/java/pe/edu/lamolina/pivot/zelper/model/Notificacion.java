package pe.edu.lamolina.pivot.zelper.model;

public class Notificacion {

    private String message;
    private int total;
    private int procesados;

    public Notificacion() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProcesados() {
        return procesados;
    }

    public void setProcesados(int procesados) {
        this.procesados = procesados;
    }

}
