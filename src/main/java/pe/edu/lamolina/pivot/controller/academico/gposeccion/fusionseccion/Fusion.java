package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import pe.edu.lamolina.model.academico.Seccion;

public class Fusion {

    private Seccion seccion;

    private Long[] alumnosid;

    private Seccion seccionSeleccionada;

    public Fusion() {
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Long[] getAlumnosid() {
        return alumnosid;
    }

    public void setAlumnosid(Long[] alumnosid) {
        this.alumnosid = alumnosid;
    }

    public Seccion getSeccionSeleccionada() {
        return seccionSeleccionada;
    }

    public void setSeccionSeleccionada(Seccion seccionSeleccionada) {
        this.seccionSeleccionada = seccionSeleccionada;
    }

}
