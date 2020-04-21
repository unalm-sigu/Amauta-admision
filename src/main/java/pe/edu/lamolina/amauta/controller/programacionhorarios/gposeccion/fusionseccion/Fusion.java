package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.fusionseccion;

import pe.edu.lamolina.model.academico.Seccion;

public class Fusion {

    private Seccion seccionDestino;

    private Long[] alumnosid;

    private Seccion seccionOrigen;

    public Fusion() {
    }

    public Seccion getSeccionDestino() {
        return seccionDestino;
    }

    public void setSeccionDestino(Seccion seccionDestino) {
        this.seccionDestino = seccionDestino;
    }

    public Long[] getAlumnosid() {
        return alumnosid;
    }

    public void setAlumnosid(Long[] alumnosid) {
        this.alumnosid = alumnosid;
    }

    public Seccion getSeccionOrigen() {
        return seccionOrigen;
    }

    public void setSeccionOrigen(Seccion seccionOrigen) {
        this.seccionOrigen = seccionOrigen;
    }

}
