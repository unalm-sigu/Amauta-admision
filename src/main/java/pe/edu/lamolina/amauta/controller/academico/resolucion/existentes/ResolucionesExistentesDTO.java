package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public class ResolucionesExistentesDTO {

    private Resolucion resolucion;

    private TramiteTitulo tramiteTitulo;

    private Tramite tramite;

    private ObtencionGrado obtencionGrado;

    public ResolucionesExistentesDTO() {
    }

    public ResolucionesExistentesDTO(Resolucion resolucion, TramiteTitulo tramiteTitulo) {
        this.resolucion = resolucion;
        this.tramiteTitulo = tramiteTitulo;
    }

    public ResolucionesExistentesDTO(Resolucion resolucion, TramiteTitulo tramiteTitulo, Tramite tramite) {
        this.resolucion = resolucion;
        this.tramiteTitulo = tramiteTitulo;
        this.tramite = tramite;
    }

    public ResolucionesExistentesDTO(Resolucion resolucion, TramiteTitulo tramiteTitulo, Tramite tramite, ObtencionGrado obtencionGrado) {
        this.resolucion = resolucion;
        this.tramiteTitulo = tramiteTitulo;
        this.tramite = tramite;
        this.obtencionGrado = obtencionGrado;
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
