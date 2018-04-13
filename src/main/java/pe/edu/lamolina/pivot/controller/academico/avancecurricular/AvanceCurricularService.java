package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularService {

    void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, DataSessionPivot ds);

    void generarAvanceCurricularByAlumno(Alumno alumno, DataSessionPivot ds);

    void desvincularPlanCurricular(PlanCurricular plan, DataSessionPivot ds);
}
