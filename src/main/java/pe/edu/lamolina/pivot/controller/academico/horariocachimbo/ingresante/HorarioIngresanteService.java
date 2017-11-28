package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface HorarioIngresanteService {

    List<AlumnoHorario> allAlumnoHorario(CicloAcademico cicloAcademico);

    List<Alumno> allAlumnoByAlumnoHorario(DynatableFilter filter, List<AlumnoHorario> alumnosHorario, CicloAcademico cicloAcademico);

}
