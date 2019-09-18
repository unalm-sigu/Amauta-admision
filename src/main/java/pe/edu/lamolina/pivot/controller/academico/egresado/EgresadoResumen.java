package pe.edu.lamolina.pivot.controller.academico.egresado;

public class EgresadoResumen {

    private Long pregrado;
    private Long posgrado;
    private Long visitante;
    private Long especial;

    public EgresadoResumen(Long pregrado, Long posgrado, Long visitante, Long especial) {
        this.pregrado = pregrado;
        this.posgrado = posgrado;
        this.visitante = visitante;
        this.especial = especial;
    }

    public EgresadoResumen() {
        this.pregrado = 0L;
        this.posgrado = 0L;
        this.visitante = 0L;
        this.especial = 0L;
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

    public Long getVisitante() {
        return visitante;
    }

    public void setVisitante(Long visitante) {
        this.visitante = visitante;
    }

    public Long getEspecial() {
        return especial;
    }

    public void setEspecial(Long especial) {
        this.especial = especial;
    }

}
