package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import pe.edu.lamolina.pivot.model.academico.Alumno;

public class AlumnoBlocked {

    private long inicio;
    private Alumno alumno;
    private String zona;

    public AlumnoBlocked(Alumno alumno, long inicio, String zona) {
        this.inicio = inicio;
        this.alumno = alumno;
        this.zona = zona;
    }

    public long getInicio() {
        return inicio;
    }

    public void setInicio(long inicio) {
        this.inicio = inicio;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

}
