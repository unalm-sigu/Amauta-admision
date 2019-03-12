package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AporteAlumnoService {

    void generarAportes(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);
}
