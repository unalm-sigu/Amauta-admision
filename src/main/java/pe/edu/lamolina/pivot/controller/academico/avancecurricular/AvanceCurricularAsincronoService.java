package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AvanceCurricularAsincronoService {

    void procesarAlumno(AlumnoCiclo alumnoCiclo, DataSessionPivot ds);
}
