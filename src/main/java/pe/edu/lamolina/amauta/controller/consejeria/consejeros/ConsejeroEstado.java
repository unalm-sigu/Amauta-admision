package pe.edu.lamolina.amauta.controller.consejeria.consejeros;

public class ConsejeroEstado {

    private Long activos;
    private Long inactivos;

    public ConsejeroEstado() {
        this.activos = 0L;
        this.inactivos = 0L;
    }

    public ConsejeroEstado(Long activo, Long inactivo) {
        this.activos = activo;
        this.inactivos = inactivo;

        this.activos = (this.activos == null) ? 0 : this.activos;
        this.inactivos = (this.inactivos == null) ? 0 : this.inactivos;
    }

    public Long getActivos() {
        return activos;
    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    public Long getInactivos() {
        return inactivos;
    }

    public void setInactivos(Long inactivos) {
        this.inactivos = inactivos;
    }

}
