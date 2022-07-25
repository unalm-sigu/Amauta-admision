package pe.edu.lamolina.amauta.controller.programacionhorarios.asignacionaula;

import java.util.List;
import pe.edu.lamolina.model.academico.Seccion;

public class SeccionesResumen {
    
    List<Seccion> secciones;
    
    int seccionesProgramadas;
    
    int seccionesAsignadas;
    
    int seccionesTipoAul;
    
    int seccionesTipoLab;

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
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
