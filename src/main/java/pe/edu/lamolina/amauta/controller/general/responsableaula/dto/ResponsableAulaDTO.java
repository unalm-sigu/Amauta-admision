package pe.edu.lamolina.amauta.controller.general.responsableaula.dto;

import java.util.List;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.general.Persona;

public class ResponsableAulaDTO {

    private Persona persona;
    private String tipoResponsable;
    private List<TurnoAtencion> turnosAtencion;

    public ResponsableAulaDTO() {
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getTipoResponsable() {
        return tipoResponsable;
    }

    public void setTipoResponsable(String tipoResponsable) {
        this.tipoResponsable = tipoResponsable;
    }

    public List<TurnoAtencion> getTurnosAtencion() {
        return turnosAtencion;
    }

    public void setTurnosAtencion(List<TurnoAtencion> turnosAtencion) {
        this.turnosAtencion = turnosAtencion;
    }

}
