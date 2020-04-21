package pe.edu.lamolina.amauta.controller.horariocachimbo.generar;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface CrearHorarioService {

    void saveHorario(
            AlumnoHorario alumno,
            List<Curso> cursos,
            List<Seccion> horarioTempo,
            Carrera carrera,
            CicloAcademico ciclo,
            Map<String, HorarioCachimbos> mapHorario,
            Map<Long, CarreraCachimbos> mapCarreraCachimbos,
            Map<Long, List<VacanteAlumno>> vacanteAlumnosMap,
            Acumulador code, DataSessionPivot ds);

    void saveHorarioFallido(
            Map<Long, Map<String, String>> mapFallidosCarrera,
            List<Carrera> carreras,
            CicloAcademico ciclo,
            DataSessionPivot ds);

}
