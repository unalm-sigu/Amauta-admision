package pe.edu.lamolina.amauta.controller.academico.plancalificacurso;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface PlanCalificaCursoService {

    void reasignarPlanDocenteCurso(CicloAcademico ciclo, DataSessionPivot ds);

}
