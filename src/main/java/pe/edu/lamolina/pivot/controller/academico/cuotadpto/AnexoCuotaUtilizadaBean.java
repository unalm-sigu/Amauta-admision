package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

public class AnexoCuotaUtilizadaBean {

    private Long idAnexo;
    private String grupa;
    private Long cantidad;

    public AnexoCuotaUtilizadaBean(Long idAnexo, Long cantidad) {
        this.idAnexo = idAnexo;
        this.cantidad = cantidad;
    }

    public AnexoCuotaUtilizadaBean(Long idAnexo, String grupa, Long cantidad) {
        this.idAnexo = idAnexo;
        this.grupa = grupa;
        this.cantidad = cantidad;
    }

    public Long getAnexoBoletin() {
        return idAnexo;
    }

    public void setAnexoBoletin(Long idAnexo) {
        this.idAnexo = idAnexo;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

}
