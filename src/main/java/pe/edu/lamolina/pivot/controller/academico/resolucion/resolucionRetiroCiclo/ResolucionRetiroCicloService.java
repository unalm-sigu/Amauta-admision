package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionRetiroCiclo;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResolucionRetiroCicloService {

    public List<Alumno> allAlumnoDesertorByNombre(String nombre, Long instanciaOficina);

    public List<Alumno> save(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    public List<Alumno> allAlumno(String nombre, Long instanciaOficina);

}
