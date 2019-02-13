package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import org.springframework.stereotype.Component;

@Component
public class VisorMuestrasLab {

    private Long numeroLab;

    public VisorMuestrasLab() {
    }

    public void incrementaNumLab() {
        this.numeroLab++;
    }

    public Long getNumeroLab() {
        return numeroLab;
    }

    public void setNumeroLab(Long numeroLab) {
        this.numeroLab = numeroLab;
    }

}
