package pe.edu.lamolina.amauta.controller.academico.egresado;

public class EgresadoResumen {

    private Long pregrado;
    private Long posgrado;

    public EgresadoResumen(Long pregrado, Long posgrado) {
        this.pregrado = pregrado;
        this.posgrado = posgrado;
    }

    public EgresadoResumen() {
        this.pregrado = 0L;
        this.posgrado = 0L;
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

}
