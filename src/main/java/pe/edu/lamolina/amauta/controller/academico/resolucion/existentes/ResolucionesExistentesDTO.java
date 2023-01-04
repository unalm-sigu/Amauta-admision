package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.*;

public class ResolucionesExistentesDTO {

    private Alumno alumno;
    private Resolucion resolucion;

    private TramiteTitulo tramiteTitulo;

    private TramiteBachiller tramiteBachiller;

    private Tramite tramite;

    private ObtencionGrado obtencionGrado;

    public ResolucionesExistentesDTO() {
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Resolucion getResolucion() {
        return resolucion;
    }

    public void setResolucion(Resolucion resolucion) {
        this.resolucion = resolucion;
    }

    public TramiteTitulo getTramiteTitulo() {
        return tramiteTitulo;
    }

    public void setTramiteTitulo(TramiteTitulo tramiteTitulo) {
        this.tramiteTitulo = tramiteTitulo;
    }

    public Tramite getTramite() {
        return tramite;
    }

    public TramiteBachiller getTramiteBachiller() {
        return tramiteBachiller;
    }

    public void setTramiteBachiller(TramiteBachiller tramiteBachiller) {
        this.tramiteBachiller = tramiteBachiller;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }

    public ObtencionGrado getObtencionGrado() {
        return obtencionGrado;
    }

    public void setObtencionGrado(ObtencionGrado obtencionGrado) {
        this.obtencionGrado = obtencionGrado;
    }

}
