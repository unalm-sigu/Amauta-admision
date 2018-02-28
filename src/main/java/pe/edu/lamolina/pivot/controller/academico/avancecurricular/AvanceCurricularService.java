package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularService {

    public void generarAvanceCurricularByPlanCurricular(PlanCurricular planCurricular, DataSessionPivot ds);

    public void generarAvanceCurricularByAlumno(Alumno alumno, DataSessionPivot ds);
}
