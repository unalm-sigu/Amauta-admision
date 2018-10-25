package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface FusionSeccionService {

    List<Alumno> allAlumnoBySeccion(Seccion seccion);

    void trasladar(Fusion fusion, CicloAcademico cicloActivo, DataSessionPivot ds);

    List<Seccion> allSeccionDisponible(Seccion seccion);

}
