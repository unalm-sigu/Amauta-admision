package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Seccion;

public class AmpliacionVacanteForm {
    
    private Seccion seccion;
    
    private List<Alumno> alumnos;

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
