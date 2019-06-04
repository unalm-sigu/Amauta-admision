package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

public class AnexoCuotaUtilizadaBean {

    private Long idAnexo;
    private String grupo;
    private Long cantidad;

    public AnexoCuotaUtilizadaBean(Long idAnexo, Long cantidad) {
        this.idAnexo = idAnexo;
        this.cantidad = cantidad;
    }

    public AnexoCuotaUtilizadaBean(Long idAnexo, String grupo, Long cantidad) {
        this.idAnexo = idAnexo;
        this.grupo = grupo;
        this.cantidad = cantidad;
    }

    public Long getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(Long idAnexo) {
        this.idAnexo = idAnexo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

}
