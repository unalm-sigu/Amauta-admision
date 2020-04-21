package pe.edu.lamolina.amauta.controller.academico.cuotagpohoras;

public class LetraCuotaUtilizadaBean {

    private String letra;
    private String grupo;
    private Long cantidadTeoria;
    private Long cantidadPractica;
    private Long idAnexo;

    public LetraCuotaUtilizadaBean(Long idAnexo, String letra, Long cantidadTeoria, Long cantidadPractica) {
        this.idAnexo = idAnexo;
        this.letra = letra;
        this.cantidadTeoria = cantidadTeoria;
        this.cantidadPractica = cantidadPractica;
    }

    public LetraCuotaUtilizadaBean(String letra, Long cantidadTeoria, Long cantidadPractica) {
        this.letra = letra;
        this.cantidadTeoria = cantidadTeoria;
        this.cantidadPractica = cantidadPractica;
    }

    public LetraCuotaUtilizadaBean(String letra, String grupo, Long cantidadTeoria, Long cantidadPractica) {
        this.letra = letra;
        this.grupo = grupo;
        this.cantidadTeoria = cantidadTeoria;
        this.cantidadPractica = cantidadPractica;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
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

    public Long getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(Long idAnexo) {
        this.idAnexo = idAnexo;
    }

    public String getAnexoLetra() {
        return this.idAnexo + "-" + this.letra;
    }

}
