package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import pe.edu.lamolina.pivot.model.academico.Carrera;

public class CarreraCursoCachimbo {

    private Carrera carrera;
    private Integer cantidad;

    public CarreraCursoCachimbo() {
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
