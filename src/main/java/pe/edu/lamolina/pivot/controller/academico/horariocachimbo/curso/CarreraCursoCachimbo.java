package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.curso;

import java.util.List;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

public class CarreraCursoCachimbo {

    private Carrera carrera;
    private Integer cantidad;
    //para fines de agregar  SeccionCursoCachimbo
    private CursoCachimbos curso;
    private List<Seccion> secciones;

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

    public CursoCachimbos getCurso() {
        return curso;
    }

    public void setCurso(CursoCachimbos curso) {
        this.curso = curso;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }

}
