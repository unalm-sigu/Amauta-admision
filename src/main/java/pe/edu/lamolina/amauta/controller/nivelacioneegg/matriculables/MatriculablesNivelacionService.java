package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface MatriculablesNivelacionService {

    List<NotaAlumnoNivelacion> allMatriculablesByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    MatriculablesResumen resumen(CicloAcademico ciclo, DataSessionPivot ds);

    int generarMatriculables(CicloAcademico ciclo, DataSessionPivot ds);

    int matriculaMasivaTipo1(CicloAcademico ciclo, DataSessionPivot ds);

    void matricularCurso(NotaAlumnoNivelacion alumnoCurso, CicloAcademico ciclo, DataSessionPivot ds);

    void retirarCurso(NotaAlumnoNivelacion alumnoCurso, CicloAcademico ciclo, DataSessionPivot ds);

}
