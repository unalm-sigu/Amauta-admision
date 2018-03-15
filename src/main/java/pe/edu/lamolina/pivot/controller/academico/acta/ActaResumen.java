package pe.edu.lamolina.pivot.controller.academico.acta;

public class ActaResumen {

    Long abierto;
    Long cerrado;
    Long pregrado;
    Long posgrado;

    public Long getAbierto() {
        return abierto;
    }

    public void setAbierto(Long abierto) {
        this.abierto = abierto;
    }

    public Long getCerrado() {
        return cerrado;
    }

    public void setCerrado(Long cerrado) {
        this.cerrado = cerrado;
    }

    public Long getPregrado() {
        return pregrado;
    }

    public void setPregrado(Long pregrado) {
        this.pregrado = pregrado;
    }

    public Long getPosgrado() {
        return posgrado;
    }

    public void setPosgrado(Long posgrado) {
        this.posgrado = posgrado;
    }

    public ActaResumen(Long abierto, Long cerrado, Long pregrado, Long posgrado) {
        this.abierto = abierto;
        this.cerrado = cerrado;
        this.pregrado = pregrado;
        this.posgrado = posgrado;
    }

}
