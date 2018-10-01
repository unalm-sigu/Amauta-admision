package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;

public interface CuotasAlumnoService {

    List<Alumno> allAlumnosPosgrado(DynatableFilter filter, CicloAcademico cicloAcademico);

}
