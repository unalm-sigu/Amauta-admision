package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularService {

    public void generarAvanceCurricular(PlanCurricular planCurricular, CicloAcademico cicloAcademico, DataSessionPivot ds);
}
