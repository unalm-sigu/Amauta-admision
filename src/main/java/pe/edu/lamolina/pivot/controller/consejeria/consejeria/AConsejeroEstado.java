
package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

public class AConsejeroEstado {
    
    private Long aconsejados;
    private Long sinConsejeros;

    public AConsejeroEstado() {
    }

    public AConsejeroEstado(Long aconsejados, Long sinConsejeros) {
        this.aconsejados = aconsejados;
        this.sinConsejeros = sinConsejeros;
    }
    
    public Long getAconsejados() {
        return aconsejados;
    }

    public void setAconsejados(Long aconsejados) {
        this.aconsejados = aconsejados;
    }

    public Long getSinConsejeros() {
        return sinConsejeros;
    }

    public void setSinConsejeros(Long sinConsejeros) {
        this.sinConsejeros = sinConsejeros;
    }
}
