package pe.edu.lamolina.pivot.controller.matricula.matriculable;

public class MatriculableResumen {

    private Long pregrado;
    private Long postgrado;
    private Long visitante;
    private Long especiales;

    public MatriculableResumen(Long pregrado, Long postgrado, Long visitante, Long especiales) {
        this.pregrado = pregrado;
        this.postgrado = postgrado;
        this.visitante = visitante;
        this.especiales = especiales;

    }

    public Long getPregrado() {
        return pregrado;
    }

    public void setPregrado(Long pregrado) {
        this.pregrado = pregrado;
    }

    public Long getPostgrado() {
        return postgrado;
    }

    public void setPostgrado(Long postgrado) {
        this.postgrado = postgrado;
    }

    public Long getVisitante() {
        return visitante;
    }

    public void setVisitante(Long visitante) {
        this.visitante = visitante;
    }

    public Long getEspeciales() {
        return especiales;
    }

    public void setEspeciales(Long especiales) {
        this.especiales = especiales;
    }

}
