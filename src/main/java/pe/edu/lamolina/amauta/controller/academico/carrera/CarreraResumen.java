package pe.edu.lamolina.amauta.controller.academico.carrera;

public class CarreraResumen {

    private Long pregrados;
    private Long posgrados;
    private Long especiales;
    private Long visitantes;

    public CarreraResumen(Long pregrados, Long posgrados, Long especiales, Long visitantes) {
        this.pregrados = pregrados;
        this.posgrados = posgrados;
        this.especiales = especiales;
        this.visitantes = visitantes;
    }

    public Long getPregrados() {
        return pregrados;
    }

    public void setPregrados(Long pregrados) {
        this.pregrados = pregrados;
    }

    public Long getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(Long visitantes) {
        this.visitantes = visitantes;
    }

    public Long getPosgrados() {
        return posgrados;
    }

    public void setPosgrados(Long posgrados) {
        this.posgrados = posgrados;
    }

    public Long getEspeciales() {
        return especiales;
    }

    public void setEspeciales(Long especiales) {
        this.especiales = especiales;
    }

}
