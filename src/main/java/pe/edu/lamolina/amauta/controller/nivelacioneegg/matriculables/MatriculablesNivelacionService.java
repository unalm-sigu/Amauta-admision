package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.BuscarCruceDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.PlantillaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface MatriculablesNivelacionService {

    CicloAcademico findCiclo(CicloAcademico ciclo);

    List<PlantillaNivelacion> allPlantillas();

    List<NotaAlumnoNivelacion> allMatriculablesByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    MatriculablesResumen resumen(CicloAcademico ciclo, DataSessionPivot ds);

    int generarMatriculables(CicloAcademico ciclo, DataSessionPivot ds);

    int matriculaMasivaTipo1(CicloAcademico ciclo, DataSessionPivot ds);

    NotaAlumnoNivelacion infoAlumno(NotaAlumnoNivelacion alumnoNivelacion, CicloAcademico ciclo, DataSessionPivot ds);

    String verificarCruce(BuscarCruceDTO buscarCruce, CicloAcademico ciclo, DataSessionPivot ds);

    List<CursoNivelacion> allSecciones(CursoNivelacion cursoNivelacion, CicloAcademico ciclo, DataSessionPivot ds);

    void matricularCurso(NotaAlumnoNivelacion alumnoCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void retirarCurso(NotaAlumnoNivelacion alumnoCurso, CursoNivelacion seccion, CicloAcademico ciclo, DataSessionPivot ds);

}
