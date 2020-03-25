package pe.edu.lamolina.pivot.controller.horariocachimbo.ingresante;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatriculaIngresanteService {

    void matricularAlumno(
            AlumnoHorario aluHorario,
            Map<Long, List<Seccion>> mapSeccion,
            List<Curso> cursos,
            List<String> erroresAlu,
            HorarioCachimbos horario,
            CicloAcademico cicloAcademico,
            DataSessionPivot ds);

}
