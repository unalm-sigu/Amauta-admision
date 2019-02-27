
package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

public class AlumnoEstado {
    
    Long activo;
    Long inactivo;

    public AlumnoEstado(Long activo, Long inactivo) {
        this.activo = activo;
        this.inactivo = inactivo;
    }

    public Long getActivo() {
        return activo;
    }

    public void setActivo(Long activo) {
        this.activo = activo;
    }

    public Long getInactivo() {
        return inactivo;
    }

    public void setInactivo(Long inactivo) {
        this.inactivo = inactivo;
    }
    
}
