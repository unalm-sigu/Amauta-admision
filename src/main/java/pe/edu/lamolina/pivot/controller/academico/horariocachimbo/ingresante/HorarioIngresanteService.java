package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface HorarioIngresanteService {

    void addAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico);

    void asignarHorario(AlumnoHorario alumnoHorario, DataSessionPivot ds);

    List<AlumnoHorario> allAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico);

    void retirarHorario(AlumnoHorario alumnoHorario);

    void activarMatricula(AlumnoHorario alumnoHorario);

    void suspenderMatricula(AlumnoHorario alumnoHorario);

    List<Alumno> allAlumnoByName(String nombre);

}
