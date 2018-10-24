package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import pe.edu.lamolina.model.academico.Seccion;

public class Fusion {

    private Seccion seccion;

    private Long[] alumnosid;

    public Fusion() {
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Long[] getAlumnos() {
        return alumnosid;
    }

    public void setAlumnos(Long[] alumnos) {
        this.alumnosid = alumnos;
    }

}
