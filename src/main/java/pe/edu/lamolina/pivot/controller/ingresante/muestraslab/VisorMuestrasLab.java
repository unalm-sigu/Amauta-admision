package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Component
public class VisorMuestrasLab {

    private Long numeroLab;
    private CicloAcademico cicloAcademico;

    public VisorMuestrasLab() {
    }

    public void incrementaNumLab() {
        this.numeroLab++;
    }

    public void decrementaNumLab() {
        this.numeroLab--;
    }

    public Long getNumeroLab() {
        return numeroLab;
    }

    public void setNumeroLab(Long numeroLab) {
        this.numeroLab = numeroLab;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

}
