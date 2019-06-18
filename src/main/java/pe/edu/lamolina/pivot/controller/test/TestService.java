package pe.edu.lamolina.pivot.controller.test;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TestService {

    void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds);

    void trasladarMatriculaCursoForPromedios(DataSessionPivot ds, Long alumnoId);

    void trasladarMatriculaCursoForPromedios(DataSessionPivot ds);

    void promediarciclocod(String cicloCod, DataSessionPivot ds);

    void promediarepgfull(DataSessionPivot ds);

    void promediarfull(DataSessionPivot ds);

    void promediarAll(Long cicloId, DataSessionPivot ds);

    void calcularAllPromediosByCiclo(DataSessionPivot ds);

    public void promediarfullBySituacion(String sit, DataSessionPivot ds);

    public void promediarepgfullBySituacion(String sit, DataSessionPivot ds);

}
