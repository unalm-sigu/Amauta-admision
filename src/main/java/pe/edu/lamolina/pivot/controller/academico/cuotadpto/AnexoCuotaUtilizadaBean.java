package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

public class AnexoCuotaUtilizadaBean {

    private Long idAnexo;
    private String grupo;
    private Long cantidadTeoria;
    private Long cantidadPractica;

    public AnexoCuotaUtilizadaBean(Long idAnexo, Long cantidadTeoria, Long cantidadPractica) {
        this.idAnexo = idAnexo;
        this.cantidadTeoria = cantidadTeoria;
        this.cantidadPractica = cantidadPractica;
    }

    public AnexoCuotaUtilizadaBean(Long idAnexo, String grupo, Long cantidadTeoria, Long cantidadPractica) {
        this.idAnexo = idAnexo;
        this.grupo = grupo;
        this.cantidadTeoria = cantidadTeoria;
        this.cantidadPractica = cantidadPractica;
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

    public Long getCantidadTeoria() {
        return cantidadTeoria;
    }

    public void setCantidadTeoria(Long cantidadTeoria) {
        this.cantidadTeoria = cantidadTeoria;
    }

    public Long getCantidadPractica() {
        return cantidadPractica;
    }

    public void setCantidadPractica(Long cantidadPractica) {
        this.cantidadPractica = cantidadPractica;
    }

}
