package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularService {

    public void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, CicloAcademico cicloAcademico, DataSessionPivot ds);

    public void generarAvanceCurricularByAlumnoCiclo(AlumnoCiclo alumnoCiclo, DataSessionPivot ds);
}
