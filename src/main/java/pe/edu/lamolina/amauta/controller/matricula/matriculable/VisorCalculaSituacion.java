package pe.edu.lamolina.amauta.controller.matricula.matriculable;

import org.springframework.stereotype.Component;

@Component
public class VisorCalculaSituacion {

    private Integer total;
    private Integer avance;

    public VisorCalculaSituacion() {
        this.total = 0;
        this.avance = 0;
    }

    public synchronized boolean iniciar(Integer total) {
        if (this.total != 0) {
            return false;
        }
        this.total = total;
        this.avance = 0;
        return true;
    }

    public synchronized void incrementar() {
        if (!this.finalizo()) {
            this.avance++;
        }
    }

    public boolean finalizo() {
        return this.total == this.avance;
    }

    public synchronized void cerrar() {
        if (this.finalizo()) {
            this.total = 0;
            this.avance = 0;
        }
    }

    public String reporte() {
        if (this.finalizo()) {
            return "Sin actividad";
        }
        return this.avance + " de " + this.total;
    }

}
