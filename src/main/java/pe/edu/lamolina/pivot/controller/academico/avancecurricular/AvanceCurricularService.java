package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PlanCurricular;

public interface AvanceCurricularService {

    public void procesarAlumnos(PlanCurricular planCurricular, CicloAcademico cicloAcademico);
}
