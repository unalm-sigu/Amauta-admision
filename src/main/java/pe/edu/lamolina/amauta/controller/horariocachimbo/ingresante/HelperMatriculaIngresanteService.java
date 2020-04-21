package pe.edu.lamolina.amauta.controller.horariocachimbo.ingresante;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface HelperMatriculaIngresanteService {

    void registrarMatricula(AlumnoHorario aluHorario, HorarioCachimbos horario, DataSessionPivot ds);

    void registrarIncrementoHorario(HorarioCachimbos horario, DataSessionPivot ds);

    void registrarErroresAlumno(AlumnoHorario aluHorario, List<String> erroresAlu, DataSessionPivot ds);

}
