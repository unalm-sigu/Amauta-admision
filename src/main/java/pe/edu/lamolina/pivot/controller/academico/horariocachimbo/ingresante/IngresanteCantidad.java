package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import pe.edu.lamolina.pivot.zelper.constant.Constantine;

public class IngresanteCantidad {

    private Integer idgen;
    private String estado;
    private String nombre;
    private Integer cantidad;

    public IngresanteCantidad() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getColor() {

        if (this.idgen == null) {
            this.idgen = 0;
        }
        if (this.idgen > 12) {
            this.idgen = this.idgen % 12;
        }
        return Constantine.MORE_FLAT_COLOR[this.idgen];
    }

    public Integer getIdgen() {
        return idgen;
    }

    public void setIdgen(Integer idgen) {
        this.idgen = idgen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
