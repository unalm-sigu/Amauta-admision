package pe.edu.lamolina.pivot.controller.academico.matriculable;

import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaResumen;

public interface MatriculableConnector {

    void procesarPrioridadAlumno(MatriculaResumen matriculaResumen, AlumnoCiclo alumnoCiclo);

    void procesarEgresado(String codigoAlumno, String codigoCarrera, String codigoFacultad, String codigoCiclo, Egresado egresado);
}
