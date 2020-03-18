package pe.edu.lamolina.pivot.controller.horariocachimbo.ingresante;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatriculaIngresanteService {

    void registrarMatricula(AlumnoHorario aluHorario, HorarioCachimbos horario, DataSessionPivot ds);

    void registrarIncrementoHorario(HorarioCachimbos horario, DataSessionPivot ds);

    void registrarErroresAlumno(AlumnoHorario aluHorario, List<String> erroresAlu, DataSessionPivot ds);

}
