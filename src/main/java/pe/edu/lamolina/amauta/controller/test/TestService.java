package pe.edu.lamolina.amauta.controller.test;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface TestService {

    void calcularAllResumenEvaluacion(Long seccionId, CicloAcademico ciclo, DataSessionPivot ds);

    //void trasladarMatriculaCursoForPromedios(DataSessionPivot ds, Long alumnoId);
//    void trasladarMatriculaCursoForPromedios(DataSessionPivot ds);
    void promediarciclocod(String cicloCod, DataSessionPivot ds);

    void promediarciclocoderror(String cicloCod, DataSessionPivot ds);

    void promediarfull(DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum);

    void promediarAll(Long cicloId, DataSessionPivot ds);

    //void calcularAllPromediosByCiclo(DataSessionPivot ds);
    void promediarfullBySituacion(String sit, DataSessionPivot ds, ModalidadEstudioEnum modalidadEstudioEnum);

    void trasladarMatriculaCursoForPromediosCiclo(DataSessionPivot ds, String codigo, Long modalidad);

    void trasladarMatriculaCursoForPromediosAlumno(DataSessionPivot ds, Long alumnoId);

    void trasladarMatriculaCursoForPromediosReview(DataSessionPivot ds, String codCiclo);

    void revisarCurriculasCiclo(String codigoCiclo, DataSessionPivot ds);

    void revisarCurriculasCarrera(String codigoCarrera, DataSessionPivot ds);

    void loadDataSendDataHistorial(List<Alumno> alumnos, List<MatriculaResumen> resumenesAll, String token, CicloAcademico ciclo);

    void trasladarInformcionPromedioForHistorialCiclo(DataSessionPivot ds, List<Alumno> alumnos, List<MatriculaResumen> resumenesAll, String token, CicloAcademico cicloAcademico);

    CicloAcademico findCiclo(String codigo, Long modalidad);

}
