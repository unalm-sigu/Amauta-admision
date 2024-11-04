package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface ProgramacionNivelacionService {

    List<CursoNivelacion> allCursosNivelacionByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    void addCurso(CursoNivelacion cursoNivelacion, CicloAcademico ciclo, DataSessionPivot ds);

}
