package pe.edu.lamolina.pivot.controller.test;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TestService {

    void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds);

    void trasladarMatriculaCursoForPromedios(DataSessionPivot ds, Long alumnoId);

    void trasladarMatriculaCursoForPromedios(DataSessionPivot ds);

    void promediarciclocod(String cicloCod, DataSessionPivot ds);

    void promediarfull(DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum);

    void promediarAll(Long cicloId, DataSessionPivot ds);

    void calcularAllPromediosByCiclo(DataSessionPivot ds);

    public void promediarfullBySituacion(String sit, DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum);

    void trasladarMatriculaCursoForPromediosCiclo(DataSessionPivot ds, String codigo, Long modalidad);

    public void trasladarMatriculaCursoForPromediosAlumno(DataSessionPivot ds, Long alumnoId);

    public void trasladarMatriculaCursoForPromediosReview(DataSessionPivot ds);
}
