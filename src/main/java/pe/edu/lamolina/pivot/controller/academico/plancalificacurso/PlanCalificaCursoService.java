package pe.edu.lamolina.pivot.controller.academico.plancalificacurso;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PlanCalificaCursoService {

    void reasignarPlanDocenteCurso(CicloAcademico ciclo, DataSessionPivot ds);

}
