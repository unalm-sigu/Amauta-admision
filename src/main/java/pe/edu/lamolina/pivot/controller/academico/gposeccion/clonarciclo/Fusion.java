package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Seccion;

public class Fusion {
    
    private Seccion seccion;
    
    private List<Alumno> alumnos;

    public Fusion() {
    }
    
    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }
    
}
