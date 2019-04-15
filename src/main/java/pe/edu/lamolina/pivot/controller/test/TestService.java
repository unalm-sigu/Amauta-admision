package pe.edu.lamolina.pivot.controller.test;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TestService {

    void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds);

}
