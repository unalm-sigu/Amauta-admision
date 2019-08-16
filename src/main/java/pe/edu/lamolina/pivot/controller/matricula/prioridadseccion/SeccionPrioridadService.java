package pe.edu.lamolina.pivot.controller.matricula.prioridadseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface SeccionPrioridadService {

    public List<MatriculaSeccion> allMatMatriculaSeccion(String codigo, String seccion,CicloAcademico  cicloAcademico);

    public List<Seccion> allSeccionByNombre(String nombre, DataSessionPivot ds);

}
