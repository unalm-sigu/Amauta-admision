package pe.edu.lamolina.pivot.controller.academico.matriculable;

import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaResumen;

public interface MatriculableConnector {

    void procesarPrioridadAlumno(MatriculaResumen matriculaResumen);

    void procesarEgresado(String codigoAlumno, String codigoCarrera, String codigoFacultad, String codigoCiclo, Egresado egresado);
}
