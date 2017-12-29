package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

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

    public String getColor(Integer indice) {
        if (indice == null) {
            indice = 0;
        }
        if (indice > 12) {
            indice = indice % 12;
        }
        return Constantine.MORE_FLAT_COLOR[indice];
    }

}
