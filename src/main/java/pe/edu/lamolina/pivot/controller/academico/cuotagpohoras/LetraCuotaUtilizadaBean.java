package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

public class LetraCuotaUtilizadaBean {

    private String letra;
    private String grupo;
    private Long cantidad;

    public LetraCuotaUtilizadaBean(String letra, Long cantidad) {
        this.letra = letra;
        this.cantidad = cantidad;
    }
     
    public LetraCuotaUtilizadaBean(String letra, String grupo, Long cantidad) {
        this.letra = letra;
        this.grupo = grupo;
        this.cantidad = cantidad;
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

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

}
