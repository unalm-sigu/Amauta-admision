package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.util.Comparator;
import java.util.Date;
import pe.edu.lamolina.model.academico.Alumno;

public class AlumnoBlocked {

    private long inicio;
    private Alumno alumno;
    private String zona;
    private String seccion;
    private Date fechaInicio;
    private Date fechaBloqueo;
    private Date fechaDesbloqueo;
    private String estado;

    public AlumnoBlocked(Alumno alumno, long inicio, String zona) {
        this.inicio = inicio;
        this.alumno = alumno;
        this.zona = zona;
        this.fechaInicio = new Date();
    }

    public AlumnoBlocked(Alumno alumno, String seccion, long inicio) {
        this.inicio = inicio;
        this.seccion = seccion;
        this.alumno = alumno;
        this.fechaInicio = new Date();
        this.estado = "INTENTANDO";
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

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaBloqueo() {
        return fechaBloqueo;
    }

    public void setFechaBloqueo(Date fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

    public Date getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(Date fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

    public Long getLapso() {
        return System.currentTimeMillis() - this.fechaInicio.getTime();
    }

    public static class CompareAlumno implements Comparator<AlumnoBlocked> {

        @Override
        public int compare(AlumnoBlocked s1, AlumnoBlocked s2) {
            int lo = s1.getAlumno().getCodigo().compareTo(s2.getAlumno().getCodigo());
            if (lo != 0) {
                return lo;
            }
            return ((Long) s1.getInicio()).compareTo((Long) s2.inicio);
        }
    }

}
