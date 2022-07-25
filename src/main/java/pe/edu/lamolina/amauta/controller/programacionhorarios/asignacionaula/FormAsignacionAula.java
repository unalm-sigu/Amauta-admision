package pe.edu.lamolina.amauta.controller.programacionhorarios.asignacionaula;

//import java.util.List;
import pe.edu.lamolina.model.academico.AsignacionAula;
//import pe.edu.lamolina.model.academico.Seccion;

public class FormAsignacionAula {
    
    private AsignacionAula asignacionAula;
    
    private int seccionesProgramadas;
    
    private int seccionesAsignadas;
    
    private int seccionesTipoAul;
    
    private int seccionesTipoLab;

    
    public AsignacionAula getAsignacionAula() {
        return asignacionAula;
    }

    public void setAsignacionAula(AsignacionAula asignacionAula) {
        this.asignacionAula = asignacionAula;
    }

    public int getSeccionesProgramadas() {
        return seccionesProgramadas;
    }

    public void setSeccionesProgramadas(int seccionesProgramadas) {
        this.seccionesProgramadas = seccionesProgramadas;
    }

    public int getSeccionesAsignadas() {
        return seccionesAsignadas;
    }

    public void setSeccionesAsignadas(int seccionesAsignadas) {
        this.seccionesAsignadas = seccionesAsignadas;
    }

    public int getSeccionesTipoAul() {
        return seccionesTipoAul;
    }

    public void setSeccionesTipoAul(int seccionesTipoAul) {
        this.seccionesTipoAul = seccionesTipoAul;
    }

    public int getSeccionesTipoLab() {
        return seccionesTipoLab;
    }

    public void setSeccionesTipoLab(int seccionesTipoLab) {
        this.seccionesTipoLab = seccionesTipoLab;
    }
   
}
