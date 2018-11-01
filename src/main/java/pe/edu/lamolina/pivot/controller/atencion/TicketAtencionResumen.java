package pe.edu.lamolina.pivot.controller.atencion;

public class TicketAtencionResumen {

    private Long activo;
    private Long respondido;
    private Long resuelto;

    public TicketAtencionResumen(Long activo, Long respondido, Long resuelto) {
        this.activo = activo;
        this.respondido = respondido;
        this.resuelto = resuelto;
    }

    public Long getActivo() {
        return activo;
    }

    public void setActivo(Long activo) {
        this.activo = activo;
    }

    public Long getRespondido() {
        return respondido;
    }

    public void setRespondido(Long respondido) {
        this.respondido = respondido;
    }

    public Long getResuelto() {
        return resuelto;
    }

    public void setResuelto(Long resuelto) {
        this.resuelto = resuelto;
    }

}
