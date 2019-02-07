package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

public class AConsejeroEstado {

    private Long conConsejados;
    private Long consejeroRetirado;
    private Long sinConsejeros;

    public AConsejeroEstado(Long conConsejados, Long consejeroRetirado, Long sinConsejero) {
        this.conConsejados = conConsejados;
        this.consejeroRetirado = consejeroRetirado;
        this.sinConsejeros = sinConsejero;
    }

    public Long getConConsejados() {
        return conConsejados;
    }

    public void setConConsejados(Long conConsejados) {
        this.conConsejados = conConsejados;
    }

    public Long getSinConsejeros() {
        return sinConsejeros;
    }

    public void setSinConsejeros(Long sinConsejeros) {
        this.sinConsejeros = sinConsejeros;
    }

    public Long getConsejeroRetirado() {
        return consejeroRetirado;
    }

    public void setConsejeroRetirado(Long consejeroRetirado) {
        this.consejeroRetirado = consejeroRetirado;
    }

}
